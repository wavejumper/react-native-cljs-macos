(ns websocket
  (:require
   ["react-native" :refer [NativeModules NativeEventEmitter]]))

(def ^:private ws
  (aget NativeModules "WebSockets"))

(def ^:private ws-events
  (NativeEventEmitter. ws))

(defn- add-listener! [id f]
  (.addListener ws-events id f))

(defonce ^:private connections
  (atom {}))

(defn- send-event [id msg cb]
  (let [f (aget ws "sendEvent")]
    (f id msg cb)))

(defn- close! [id cb]
  (let [f (aget ws "close")]
    (f id cb)))

(defn WebSocket
  [uri opts]
  (let [noop (constantly nil)
        self (clj->js (merge {:send      noop
                              :close     noop
                              :onclose   noop
                              :onconnect noop
                              :onerror   noop
                              :onmessage noop}
                             opts))]
    (.connect ws uri
              (fn [id]
                (set! (.-send self) #(send-event id % noop))
                (set! (.-close self) #(close! id noop))
                (swap! connections assoc id self)))
    self))

(defn handle-event [handler-id event]
  (let [event-id (aget event "id")]
    (if-let [socket (get @connections event-id)]
      (if-let [f (aget socket handler-id)]
        (f event)
        (js/console.log "No event handler registered for" handler-id))
      (js/console.warn "No socket found for event" event-id))))

(add-listener!
 "onMessage"
 (partial handle-event "onmessage"))

(add-listener!
 "onError"
 (partial handle-event "onerror"))

(add-listener!
 "onConnect"
 (partial handle-event "onopen"))

(add-listener!
 "onClose"
 (partial handle-event "onclose"))

(set! js/WebSocket WebSocket)

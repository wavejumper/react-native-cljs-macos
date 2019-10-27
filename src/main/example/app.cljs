(ns example.app
  (:require
   [rehook.core :as rehook]
   ["react-native" :as rn :refer [AppRegistry]]
   ["react" :as react]))

(def react-native-components
  {:Text rn/Text
   :View rn/View})

(defn handle-component [component]
  (if (keyword? component)
    (get react-native-components component)
    component))

(defn $
  ([elem]
   (react/createElement (handle-component elem)))
  ([elem args]
   (react/createElement (handle-component elem) (clj->js args)))
  ([elem args & children]
   (apply react/createElement (handle-component elem) (clj->js args) children)))

(defn app []
  ($ :View {:style #js {:flex           1
                        :justifyContent "center"
                        :alignItems     "center"}}
     ($ :Text {}
        "Hello from Clojurescript!")))

;; Release builds don't like to remount components, AppRegistry.unmountApplicationComponentAtRootTag
;; woefully underdocumented.
(defonce root-component
  (atom {:root app}))

(defn dominant-component []
  ;; rehook/use-atom is similar to reagent.core/ratom, except leverging react hooks.
  (let [[{:keys [root]} _] (rehook/use-atom root-component)]
    ($ root)))

(defn ^:dev/after-load relaod []
  (reset! root-component {:root app}))

(defn main []
  (.registerComponent AppRegistry "app" (constantly #($ dominant-component))))

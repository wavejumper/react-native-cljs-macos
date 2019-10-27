import Foundation
import SwiftWebSocket

@objc(WebSockets)
class WebSockets: RCTEventEmitter {
  
  private var connections: [String: WebSocket]! = [:]
  
  func initEvents(id: String, ws: WebSocket) -> Void {
    ws.event.open = {
      self.sendEvent(withName: "onConnect", body: ["id": id])
    }
    ws.event.error = { error in
      self.sendEvent(withName: "onError", body: ["id": id, "error": error])
    }
    ws.event.message = { message  in
      self.sendEvent(withName: "onMessage", body: ["id": id, "data": message])
    }
    ws.event.close = { code, reason, clean in
      self.sendEvent(withName: "onClose", body: ["id": id, "code": code, "reason": reason, "clean": clean])
    }
  }
  
  override func constantsToExport() -> [AnyHashable : Any]! {
    return [:]
  }
  
  override func supportedEvents() -> [String]! {
    return ["onConnect", "onError", "onMessage", "onClose"]
  }
  
  override static func requiresMainQueueSetup() -> Bool {
    return false
  }
  
  @objc(sendEvent:msg:callback:)
  func sendEvent(id: String, msg: String,  _ callback: RCTResponseSenderBlock) -> Void {
    if let ws = self.connections[id] {
      let event = ["id": id, "success": true] as [String : Any];
      ws.send(msg)
      callback([event])
    } else {
      let event = ["id": id, "success": false] as [String : Any];
      callback([event])
    }
  }
  
  @objc(close:callback:)
  func close(id: String, _ callback: RCTResponseSenderBlock) -> Void {
    if let ws = self.connections.removeValue(forKey: id) {
      let event = ["id": id, "success": true] as [String : Any];
      ws.close()
      callback([event])
    }
    else {
      let event = ["id": id, "success": false] as [String : Any];
      callback([event])
    }
  }
  
  @objc(connect:callback:)
  func connect(uri: String, _ callback: RCTResponseSenderBlock) -> Void {
    let id = UUID().uuidString;
    let ws = WebSocket(uri);
    connections[id] = ws;
    initEvents(id: id, ws: ws);
    callback([id])
  }
}

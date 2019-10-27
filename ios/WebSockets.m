#import "React/RCTBridgeModule.h"
#import "React/RCTEventEmitter.h"

@interface RCT_EXTERN_MODULE(WebSockets, RCTEventEmitter)

RCT_EXTERN_METHOD(connect:(NSString *)uri callback:(RCTResponseSenderBlock)callback)
RCT_EXTERN_METHOD(close:(NSString *)id callback:(RCTResponseSenderBlock)callback)
RCT_EXTERN_METHOD(sendEvent:(NSString *)id msg:(NSString *)msg callback:(RCTResponseSenderBlock)callback)

@end

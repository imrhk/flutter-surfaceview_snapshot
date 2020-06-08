#import "SurfaceviewSnapshotPlugin.h"
#if __has_include(<surfaceview_snapshot/surfaceview_snapshot-Swift.h>)
#import <surfaceview_snapshot/surfaceview_snapshot-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "surfaceview_snapshot-Swift.h"
#endif

@implementation SurfaceviewSnapshotPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftSurfaceviewSnapshotPlugin registerWithRegistrar:registrar];
}
@end

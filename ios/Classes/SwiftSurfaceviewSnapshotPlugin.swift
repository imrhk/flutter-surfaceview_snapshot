import Flutter
import UIKit

public class SwiftSurfaceviewSnapshotPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "surfaceview_snapshot", binaryMessenger: registrar.messenger())
    let instance = SwiftSurfaceviewSnapshotPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    result("iOS " + UIDevice.current.systemVersion)
  }
}

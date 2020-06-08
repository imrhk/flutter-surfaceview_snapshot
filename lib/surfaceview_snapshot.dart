import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/services.dart';

class SurfaceviewSnapshot {
  static const MethodChannel _channel =
      const MethodChannel('surfaceview_snapshot');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<Uint8List> get snapshot async {
   return await _channel.invokeMethod<Uint8List>('getSnapshot');
  }
}

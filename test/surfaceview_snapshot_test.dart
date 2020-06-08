import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:surfaceview_snapshot/surfaceview_snapshot.dart';

void main() {
  const MethodChannel channel = MethodChannel('surfaceview_snapshot');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await SurfaceviewSnapshot.platformVersion, '42');
  });
}

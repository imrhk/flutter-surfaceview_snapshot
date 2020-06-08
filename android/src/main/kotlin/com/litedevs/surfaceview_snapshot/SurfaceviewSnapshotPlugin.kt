package com.litedevs.surfaceview_snapshot

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.view.PixelCopy
import android.view.SurfaceView
import android.view.ViewGroup
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.android.FlutterSurfaceView
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import java.io.ByteArrayOutputStream


/** SurfaceviewSnapshotPlugin */
public class SurfaceviewSnapshotPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel

  private lateinit var context: Context
  private lateinit var activity: Activity

  //private lateinit var

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "surfaceview_snapshot")
    channel.setMethodCallHandler(this);

    context = flutterPluginBinding.applicationContext
  }

  override fun onDetachedFromActivity() {
    TODO("Not yet implemented")
}

override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    TODO("Not yet implemented")
}

override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity;
}

override fun onDetachedFromActivityForConfigChanges() {
    TODO("Not yet implemented")
}

  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "surfaceview_snapshot")
      channel.setMethodCallHandler(SurfaceviewSnapshotPlugin())
    }
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    } 
    else if(call.method == "getSnapshot") {

       val surfaceView = findSurfaceView( activity.findViewById<ViewGroup>(android.R.id.home))
        when(surfaceView != null) {
            true -> usePixelCopy(surfaceView) { bitmap: Bitmap? ->
                bitmap?:let { result.error("Could not decode", "Could not decode", null)
                return@usePixelCopy
                }

                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray = stream.toByteArray()
                bitmap.recycle()
                result.success(byteArray)
            }
            else -> result.error("Not found", "Not found", null)
        }
    }
    else {
      result.notImplemented()
    }
  }

  private fun findSurfaceView(view : ViewGroup?) : SurfaceView? {
      view?:return null;
      for(i in 0..view.childCount) {
          val child = view.getChildAt(i)
          if(child is ViewGroup) {
              val result = findSurfaceView(child)
              if(result != null)
                  return result
          }
          else if(child is SurfaceView && child !is FlutterSurfaceView) {
              return child;
          }
      }

      return null;
    }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  private fun usePixelCopy(videoView: SurfaceView, callback: (Bitmap?) -> Unit) {
        val bitmap: Bitmap = Bitmap.createBitmap(
            videoView.width,
            videoView.height,
            Bitmap.Config.ARGB_8888
        );
        try {
        // Create a handler thread to offload the processing of the image.
        val handlerThread = HandlerThread("PixelCopier");
        handlerThread.start();
        PixelCopy.request(
            videoView, bitmap,
            PixelCopy.OnPixelCopyFinishedListener { copyResult ->
                if (copyResult == PixelCopy.SUCCESS) {
                    callback(bitmap)
                }
                handlerThread.quitSafely();
            },
            Handler(handlerThread.looper)
        )
        } catch (e: IllegalArgumentException) {
            callback(null)
            // PixelCopy may throw IllegalArgumentException, make sure to handle it
            e.printStackTrace()
        }
    }
}

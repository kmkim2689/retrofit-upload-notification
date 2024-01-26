package com.practice.retrofit_notification

import android.Manifest
import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.practice.retrofit_notification.ui.theme.RetrofitnotificationTheme
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val context = LocalContext.current



            val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS, // 알람
                )
            } else {
                emptyArray()
            }

            val launcherMultiplePermissions = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissionsMap ->
                val areGranted = permissionsMap.values.reduce { acc, next -> acc && next }
                if (areGranted) {
                    Log.d("권한", "권한이 동의되었습니다.")
                } else {
                    Log.d("권한", "권한이 거부되었습니다.")
                }
            }

            // 권한이 이미 있는 경우
            if(permissions.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }){
                Log.d("권한","권한이 이미 존재합니다.")
            }
            // 권한이 없는 경우
            else {
                Log.d("권한","권한이 존재하지 않습니다.")
                launcherMultiplePermissions.launch(permissions)
            }

            RetrofitnotificationTheme {
                // A surface container using the 'background' color from the theme

                var hasNotificationPermission by remember {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        mutableStateOf(
                            ContextCompat.checkSelfPermission(
                                context,
                                POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED
                        )
                    } else {
                        mutableStateOf(true)
                    }
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val files = listOf<File>(
                        File("path/to/your/file1.jpg"),
                        File("path/to/your/file2.jpg")
                    )

                    val requestString = "{}".toRequestBody("application/json".toMediaTypeOrNull())

                    val maxCount = files.count() + 1

                    val callback = object : UploadCallback {
                        override fun onProgressUpdate(percentage: Int) {

                        }

                        override fun onFinish() {
                            val service = ProgressNotificationService(context)

                            // progress 갱신 작업은 외부에서 진행?
                            ++UploadProgress.progress
                            service.showProgressNotification(max = maxCount, progress = UploadProgress.progress)
                        }

                        override fun onError() {

                        }
                    }

                    val file1RequestBody = UploadBody(files[0], "image/*", callback)
                    val file2RequestBody = UploadBody(files[1], "image/*",callback)
                    val requestBody = UploadBody(requestString, "application/json", callback)
                }
            }
        }
    }
}



package com.junho.app.kingcon.Etc

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtilityOptions
import com.amazonaws.services.s3.AmazonS3Client
import java.io.*

@SuppressLint("StaticFieldLeak")
object AWSS3 {
    //업로드
    fun uploadPicture(context: Context, key: String, filePath: String) {
        val options = TransferUtilityOptions()
        //스레드 수
        options.transferThreadPoolSize = 8
        //정지된 전송을 해당 주기로 감시 하여 발견시 실행 시는 시간
        options.transferServiceCheckTimeInterval = 2 * 60 * 1000 // 2-minutes

        val transferUtility = TransferUtility.builder()
            .context(context)
            .awsConfiguration(AWSMobileClient.getInstance().configuration)
            .s3Client(AmazonS3Client(AWSMobileClient.getInstance()))
            .transferUtilityOptions(options)
            .build()

        //TODO 캐시 이미지
        val path = filePath.replace(".jpg","hip.jpg")
        //리사이징 이미지 캐시 경로
        saveBitmapToFileCache(getBitmap(context, filePath), path)
        saveBitmapToFileCache(getBitmap(context, filePath), path + "cache")
        val uploadObserver = transferUtility.upload(
            "product/$key.jpg",
            File(path)
        )
        // 관찰자에게 수신기를 연결하여 상태 업데이트 및 진행률 알림 가져옴
        uploadObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState) {
                if (TransferState.COMPLETED === state) {
                    // 업로드 완료
                    Toast.makeText(context, "이미지 업로드 완료", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                //진행률
                val percentDonef = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
                val percentDone = percentDonef.toInt()
                Log.d("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                        + " bytesTotal: " + bytesTotal + " " + percentDone + "%"
                )
            }
            override fun onError(id: Int, ex: Exception) {
                // 에러
                Log.e("s3error", ex.toString())
                Toast.makeText(context, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
            }
        })

        // 데이터를 폴링하려면 앨리스터를 부착하는 대신 관찰자의 상태 및 진행 상태를 확인하십시오.
        if (TransferState.COMPLETED === uploadObserver.state) {
            // 업로드 완료
            Log.e("s3error", uploadObserver.state.toString())
            Toast.makeText(context, "이미지 업로드 완료", Toast.LENGTH_SHORT).show()
        }

        Log.d("YourActivity", "Bytes Transferred: " + uploadObserver.bytesTransferred)
        Log.d("YourActivity", "Bytes Total: " + uploadObserver.bytesTotal)
    }

    private fun saveBitmapToFileCache(bitmap: Bitmap?, path: String){
        val fileCacheItem = File(path)
        var out: OutputStream? = null
        try {
            fileCacheItem.createNewFile()
            out = FileOutputStream(fileCacheItem)

            bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, out)
        }catch (e: Exception){}
        finally {
            try {
                out?.close()
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }
    private fun getBitmap(context: Context, path :String) : Bitmap? {
        var ins: InputStream?
        try {
            val IMAGE_MAX_SIZE = 1024 * 512 //1MB
            ins = context.contentResolver.openInputStream(Uri.fromFile(File(path)))

            // Decode image size
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(ins, null, options)
            ins.close()

            var scale = 1
            while ((options.outWidth * options.outHeight) * (1 / Math.pow(scale.toDouble(), 2.0)) >
                IMAGE_MAX_SIZE) {
                scale++
            }
            Log.d("TAG", "scale = " + scale + ", orig-width: " + options.outWidth + ", orig-height: " + options.outHeight)

            var resultBitmap: Bitmap?
            ins = context.contentResolver.openInputStream(Uri.fromFile(File(path)))
            if (scale > 1) {
                scale--
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                val option = BitmapFactory.Options()
                option.inSampleSize = scale
                resultBitmap = BitmapFactory.decodeStream(ins, null, option)

                // resize to desired dimensions
                val height = resultBitmap.height
                val width = resultBitmap.width
                Log.d("TAG", "1th scale operation dimension - width: $width, height: $height")

                val y = Math.sqrt(IMAGE_MAX_SIZE/ ((width.toDouble()) / height))
                val x = (y / height) * width

                val scaledBitmap = Bitmap.createScaledBitmap(resultBitmap, x.toInt(), y.toInt(), true)
                resultBitmap.recycle()
                resultBitmap = if(x>y){
                    //방향 맞춤
                    val matrix = Matrix()
                    matrix.postRotate(90F)
                    val scaledBitmap = Bitmap.createScaledBitmap(scaledBitmap, width, height, true)
                    Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
                }else{
                    scaledBitmap
                }


                System.gc()
            } else {
                resultBitmap = BitmapFactory.decodeStream(ins)
            }
            ins.close()

            Log.d("TAG", "bitmap size - width: " +resultBitmap.width + ", height: " + resultBitmap.height)
            return resultBitmap
        } catch (e: IOException) {
            Log.e("TAG", e.message, e)
            return null
        }
    }
}
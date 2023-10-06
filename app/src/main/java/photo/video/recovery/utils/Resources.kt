package photo.video.recovery.utils

sealed class Resources<T>(val data: T? = null, val message: String? = null) {
    class Idle<T>(message: String, data: T? = null) : Resources<T>(data, message)
    class Progress<T>(val count: Int = 0) : Resources<T>(null)
    class Success<T>(data: T?) : Resources<T>(data)
    class Error<T>(message: String, data: T? = null) : Resources<T>(data,message)
}
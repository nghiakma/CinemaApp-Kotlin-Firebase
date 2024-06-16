package com.example.myapplication45.constant

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.ImageView
import com.example.myapplication45.activity.MainActivity
import com.example.myapplication45.activity.MovieDetailActivity
import com.example.myapplication45.activity.admin.AdminActivity

import com.example.myapplication45.listener.IGetDateListener
import com.example.myapplication45.model.Movie
import com.example.myapplication45.model.RoomFirebase
import com.example.myapplication45.model.Seat
import com.example.myapplication45.model.TimeFirebase
import com.example.myapplication45.prefs.DataStoreManager
import com.example.myapplication45.util.StringUtil
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import java.text.Normalizer
import java.util.Calendar
import java.util.regex.Pattern

//singleton: tao ra 1 object duy nhat

object GlobalFunction {

    fun startActivity(context: Context?, clz: Class<*>?) {
        val intent = Intent(context, clz)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        context!!.startActivity(intent)
    }
    fun hideSoftKeyboard(activity: Activity?) {
        try {
            val inputMethodManager = activity!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        } catch (ex: NullPointerException) {
            ex.printStackTrace()
        }
    }
    //loại bỏ các dấu có thể gắn liền với các ký tự gốc.
    fun getTextSearch(input: String?): String {
        val nfdNormalizedString = Normalizer.normalize(input, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(nfdNormalizedString).replaceAll("")
    }
    fun gotoMainActivity(context: Context?) {
        if (DataStoreManager.getUser()!!.isAdmin) {
            startActivity(context, AdminActivity::class.java)
        } else {
            startActivity(context, MainActivity::class.java)
        }
    }
    fun startActivity(context: Context?, clz: Class<*>?, bundle: Bundle?) {
        val intent = Intent(context, clz)
        intent.putExtras(bundle!!)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        context!!.startActivity(intent)
    }
    fun goToMovieDetail(context: Context?, movie: Movie?) {
        val bundle = Bundle()
        bundle.putSerializable(ConstantKey.KEY_INTENT_MOVIE_OBJECT, movie)
        startActivity(context, MovieDetailActivity::class.java, bundle)
    }

    fun showDatePicker(context: Context?, currentDate: String?, getDateListener: IGetDateListener) {
        val mCalendar = Calendar.getInstance()
        var currentDay = mCalendar[Calendar.DATE]
        var currentMonth = mCalendar[Calendar.MONTH]
        var currentYear = mCalendar[Calendar.YEAR]
        mCalendar[currentYear, currentMonth] = currentDay
        if (!StringUtil.isEmpty(currentDate)) {
            val split = currentDate!!.split("-".toRegex()).toTypedArray()
            currentDay = split[0].toInt()
            currentMonth = split[1].toInt()
            currentYear = split[2].toInt()
            mCalendar[currentYear, currentMonth - 1] = currentDay
        }
        val callBack =
            DatePickerDialog.OnDateSetListener { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val date =
                    StringUtil.getDoubleNumber(dayOfMonth) + "-" + StringUtil.getDoubleNumber(
                        monthOfYear + 1
                    ) + "-" + year
                getDateListener.getDate(date)
            }
        val datePicker = DatePickerDialog(context!!,
            callBack, mCalendar[Calendar.YEAR], mCalendar[Calendar.MONTH],
            mCalendar[Calendar.DATE])
        datePicker.show()
    }

    fun getListRooms(): List<RoomFirebase> {
        val list: MutableList<RoomFirebase> = ArrayList()
        list.add(RoomFirebase(1, "Phòng 1", getListTimes()))
        list.add(RoomFirebase(2, "Phòng 2", getListTimes()))
        list.add(RoomFirebase(3, "Phòng 3", getListTimes()))
        list.add(RoomFirebase(4, "Phòng 4", getListTimes()))
        list.add(RoomFirebase(5, "Phòng 5", getListTimes()))
        list.add(RoomFirebase(6, "Phòng 6", getListTimes()))
        return list
    }

    private fun getListTimes(): List<TimeFirebase> {
        val list: MutableList<TimeFirebase> = ArrayList()
        list.add(TimeFirebase(1, "7AM - 8AM", getListSeats()))
        list.add(TimeFirebase(2, "8AM - 9AM", getListSeats()))
        list.add(TimeFirebase(3, "9AM - 10AM", getListSeats()))
        list.add(TimeFirebase(4, "10AM - 11AM", getListSeats()))
        list.add(TimeFirebase(5, "1PM - 2PM", getListSeats()))
        list.add(TimeFirebase(6, "2PM - 3PM", getListSeats()))
        return list
    }

    private fun getListSeats(): List<Seat> {
        val list: MutableList<Seat> = ArrayList()
        list.add(Seat(1, "1", false))
        list.add(Seat(2, "2", false))
        list.add(Seat(3, "3", false))
        list.add(Seat(4, "4", false))
        list.add(Seat(5, "5", false))
        list.add(Seat(6, "6", false))
        list.add(Seat(7, "7", false))
        list.add(Seat(8, "8", false))
        list.add(Seat(9, "9", false))
        list.add(Seat(10, "10", false))
        list.add(Seat(11, "11", false))
        list.add(Seat(12, "12", false))
        list.add(Seat(13, "13", false))
        list.add(Seat(14, "14", false))
        list.add(Seat(15, "15", false))
        list.add(Seat(16, "16", false))
        list.add(Seat(17, "17", false))
        list.add(Seat(18, "18", false))
        return list
    }

    fun gentQRCodeFromString(imageView: ImageView?, id: String?) {
        if (imageView == null) {
            return
        }
        val result: BitMatrix
        try {
            result = MultiFormatWriter().encode(id, BarcodeFormat.QR_CODE,
                512, 512, null)
            val w = result.width
            val h = result.height
            val pixels = IntArray(w * h)
            for (y in 0 until h) {
                val offset = y * w
                for (x in 0 until w) {
                    pixels[offset + x] = if (result[x, y]) Color.BLACK else Color.WHITE
                }
            }
            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
            imageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
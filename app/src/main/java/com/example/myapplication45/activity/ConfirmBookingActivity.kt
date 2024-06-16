package com.example.myapplication45.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication45.MyApplication
import com.example.myapplication45.R
import com.example.myapplication45.adapter.*
import com.example.myapplication45.constant.ConstantKey
import com.example.myapplication45.constant.GlobalFunction
import com.example.myapplication45.constant.PayPalConfig
import com.example.myapplication45.databinding.ActivityConfirmBookingBinding
import com.example.myapplication45.listener.IOnSingleClickListener
import com.example.myapplication45.model.*
import com.example.myapplication45.prefs.DataStoreManager
import com.example.myapplication45.util.StringUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.paypal.android.sdk.payments.*
import org.json.JSONException
import org.json.JSONObject
import java.math.BigDecimal
import java.util.*


class ConfirmBookingActivity : AppCompatActivity() {

    private var mDialog: Dialog? = null
    private var mActivityConfirmBookingBinding: ActivityConfirmBookingBinding? = null
    private var mMovie: Movie? = null
    private var mListRooms: List<Room>? = null
    private var mRoomAdapter: RoomAdapter? = null
    private var mTitleRoomSelected: String? = null
    private var mListTimes: List<SlotTime>? = null
    private var mTimeAdapter: TimeAdapter? = null
    private var mTitleTimeSelected: String? = null
    private var mListFood: MutableList<Food>? = null
    private var mFoodDrinkAdapter: FoodDrinkAdapter? = null
    private var mListSeats: List<SeatLocal>? = null
    private var mSeatAdapter: SeatAdapter? = null
    private var mPaymentMethodSelected: PaymentMethod? = null
    private var mBookingHistory: BookingHistory? = null
    private var mListFoodNeedUpdate: List<Food>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityConfirmBookingBinding = ActivityConfirmBookingBinding.inflate(layoutInflater)
        setContentView(mActivityConfirmBookingBinding!!.root)
        getDataIntent()
    }

    private fun getDataIntent() {
        val bundle = intent.extras ?: return
        val movie = bundle[ConstantKey.KEY_INTENT_MOVIE_OBJECT] as Movie?
        getMovieInformation(movie!!.id)
    }

    private fun getMovieInformation(movieId: Long) {
        MyApplication[this].getMovieDatabaseReference().child(movieId.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mMovie = snapshot.getValue(Movie::class.java)
                    displayDataMovie()
                    initListener()
                    initSpinnerCategory()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun displayDataMovie() {
        if (mMovie == null) {
            return
        }
        mActivityConfirmBookingBinding!!.tvMovieName.text = mMovie?.name
        val strPrice = mMovie?.price.toString() + ConstantKey.UNIT_CURRENCY_MOVIE
        mActivityConfirmBookingBinding!!.tvMoviePrice.text = strPrice
        showListRooms()
        initListFoodAndDrink()
    }

    private fun initListener() {
        mActivityConfirmBookingBinding!!.imgBack.setOnClickListener { onBackPressed() }
        mActivityConfirmBookingBinding!!.btnConfirm.setOnClickListener { onClickBookingMovie() }
    }

    private fun showListRooms() {
        val gridLayoutManager = GridLayoutManager(this, 2)
        mActivityConfirmBookingBinding!!.rcvRoom.layoutManager = gridLayoutManager
        mListRooms = getListRoomLocal()
        mRoomAdapter = RoomAdapter(mListRooms, object : RoomAdapter.IManagerRoomListener{
            override fun clickItemRoom(room: Room) {
                onClickSelectRoom(room)
            }
        })
        mActivityConfirmBookingBinding!!.rcvRoom.adapter = mRoomAdapter
    }

    private fun getListRoomLocal() : List<Room> {
        val list: MutableList<Room> = java.util.ArrayList()
        if (mMovie?.rooms != null) {
            for (roomFirebase in mMovie?.rooms!!) {
                val room = Room(roomFirebase!!.id, roomFirebase.title, false)
                list.add(room)
            }
        }
        return list
    }

    private fun getTitleRoomSelected() : String? {
        for (room in mListRooms!!) {
            if (room.isSelected) {
                mTitleRoomSelected = room.title
                break
            }
        }
        return mTitleRoomSelected
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onClickSelectRoom(room: Room) {
        for (i in mListRooms!!.indices) {
            mListRooms!![i].isSelected = (mListRooms!![i].id == room.id)
        }
        mRoomAdapter!!.notifyDataSetChanged()
        showListTimes(room.id)
    }

    private fun getRoomFirebaseFromId(roomId: Int): RoomFirebase? {
        var roomFirebase: RoomFirebase? = RoomFirebase()
        if (mMovie?.rooms != null) {
            for (roomFirebaseEntity in mMovie?.rooms!!) {
                if (roomFirebaseEntity!!.id == roomId) {
                    roomFirebase = roomFirebaseEntity
                    break
                }
            }
        }
        return roomFirebase
    }

    private fun showListTimes(roomId: Int) {
        mActivityConfirmBookingBinding!!.layoutSelecteTime.visibility = View.VISIBLE
        mActivityConfirmBookingBinding!!.layoutSelecteSeat.visibility = View.GONE
        val gridLayoutManager = GridLayoutManager(this, 2)
        mActivityConfirmBookingBinding!!.rcvTime.layoutManager = gridLayoutManager
        mListTimes = getListTimeLocal(roomId)
        mTimeAdapter = TimeAdapter(mListTimes, object : TimeAdapter.IManagerTimeListener{
            override fun clickItemTime(time: SlotTime) {
                onClickSelectTime(time)
            }
        })
        mActivityConfirmBookingBinding!!.rcvTime.adapter = mTimeAdapter
    }

    private fun getListTimeLocal(roomId: Int): List<SlotTime> {
        val list: MutableList<SlotTime> = java.util.ArrayList()
        val roomFirebase = getRoomFirebaseFromId(roomId)
        if (roomFirebase?.times != null) {
            for (timeFirebase in roomFirebase.times!!) {
                val slotTime = SlotTime(timeFirebase.id, timeFirebase.title,
                    false, roomId)
                list.add(slotTime)
            }
        }
        return list
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onClickSelectTime(time: SlotTime) {
        for (i in mListTimes!!.indices) {
            mListTimes!![i].isSelected = mListTimes!![i].id == time.id
        }
        mTimeAdapter!!.notifyDataSetChanged()
        showListSeats(time)
    }

    private fun getTitleTimeSelected() : String? {
        for (time in mListTimes!!) {
            if (time.isSelected) {
                mTitleTimeSelected = time.title
                break
            }
        }
        return mTitleTimeSelected
    }

    private fun showListSeats(time: SlotTime) {
        mActivityConfirmBookingBinding!!.layoutSelecteSeat.visibility = View.VISIBLE
        val gridLayoutManager = GridLayoutManager(this, 6)
        mActivityConfirmBookingBinding!!.rcvSeat.layoutManager = gridLayoutManager
        mListSeats = getListSeatLocal(time)
        mSeatAdapter = SeatAdapter(mListSeats, object : SeatAdapter.IManagerSeatListener{
            override fun clickItemSeat(seat: SeatLocal) {
                onClickItemSeat(seat)
            }
        })
        mActivityConfirmBookingBinding!!.rcvSeat.adapter = mSeatAdapter
    }

    private fun getListSeatLocal(time: SlotTime): List<SeatLocal> {
        val roomFirebase = getRoomFirebaseFromId(time.roomId)
        val timeFirebase = getTimeFirebaseFromId(roomFirebase, time.id)
        val list: MutableList<SeatLocal> = java.util.ArrayList()
        if (timeFirebase?.seats != null) {
            for (seat in timeFirebase.seats!!) {
                val seatLocal = SeatLocal(seat.id, seat.title,
                    seat.isSelected, time.roomId, time.id)
                list.add(seatLocal)
            }
        }
        return list
    }

    private fun getTimeFirebaseFromId(roomFirebase: RoomFirebase?, timeId: Int): TimeFirebase? {
        var timeFirebase: TimeFirebase? = TimeFirebase()
        if (roomFirebase?.times != null) {
            for (timeFirebaseEntity in roomFirebase.times!!) {
                if (timeFirebaseEntity.id == timeId) {
                    timeFirebase = timeFirebaseEntity
                    break
                }
            }
        }
        return timeFirebase
    }

    private fun getSeatFirebaseFromId(roomId: Int, timeId: Int, seatId: Int): Seat? {
        val roomFirebase = getRoomFirebaseFromId(roomId)
        val timeFirebase = getTimeFirebaseFromId(roomFirebase, timeId)
        var seatResult: Seat? = Seat()
        if (timeFirebase?.seats != null) {
            for (seat in timeFirebase.seats!!) {
                if (seat.id == seatId) {
                    seatResult = seat
                    break
                }
            }
        }
        return seatResult
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onClickItemSeat(seat: SeatLocal) {
        if (seat.isSelected) {
            return
        }
        seat.isChecked = !seat.isChecked
        mSeatAdapter!!.notifyDataSetChanged()
    }

    private fun initListFoodAndDrink() {
        val linearLayoutManager = LinearLayoutManager(this)
        mActivityConfirmBookingBinding!!.rcvFoodDrink.layoutManager = linearLayoutManager
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        mActivityConfirmBookingBinding!!.rcvFoodDrink.addItemDecoration(decoration)
        getListFoodAndDrink()
    }

    private fun getListFoodAndDrink() {
        MyApplication[this].getFoodDatabaseReference().addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (mListFood != null) {
                    mListFood!!.clear()
                } else {
                    mListFood = java.util.ArrayList()
                }
                for (dataSnapshot in snapshot.children) {
                    val food = dataSnapshot.getValue(Food::class.java)
                    if (food != null && food.quantity > 0) {
                        mListFood!!.add(0, food)
                    }
                }
                mFoodDrinkAdapter = FoodDrinkAdapter(mListFood, object : FoodDrinkAdapter.IManagerFoodDrinkListener{
                    override fun selectCount(food: Food, count: Int) {
                        selectedCountFoodAndDrink(food, count)
                    }
                })
                mActivityConfirmBookingBinding!!.rcvFoodDrink.adapter = mFoodDrinkAdapter
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun selectedCountFoodAndDrink(food: Food, count: Int) {
        if (mListFood == null || mListFood!!.isEmpty()) {
            return
        }
        for (foodEntity in mListFood!!) {
            if (foodEntity.id == food.id) {
                foodEntity.count = count
                break
            }
        }
    }

    private fun initSpinnerCategory() {
        val list: MutableList<PaymentMethod> = java.util.ArrayList()
        list.add(PaymentMethod(ConstantKey.PAYMENT_CASH, ConstantKey.PAYMENT_CASH_TITLE))
        list.add(PaymentMethod(ConstantKey.PAYMENT_PAYPAL, ConstantKey.PAYMENT_PAYPAL_TITLE))
        val selectPaymentAdapter = SelectPaymentAdapter(this,
            R.layout.item_choose_option, list)
        mActivityConfirmBookingBinding!!.spnPayment.adapter = selectPaymentAdapter
        mActivityConfirmBookingBinding!!.spnPayment.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                mPaymentMethodSelected = selectPaymentAdapter.getItem(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun onClickBookingMovie() {
        if (mMovie == null) {
            return
        }
        if (StringUtil.isEmpty(getTitleRoomSelected())) {
            Toast.makeText(this, getString(R.string.msg_select_room_require), Toast.LENGTH_SHORT).show()
            return
        }
        if (StringUtil.isEmpty(getTitleTimeSelected())) {
            Toast.makeText(this, getString(R.string.msg_select_time_require), Toast.LENGTH_SHORT).show()
            return
        }
        val countSeat = getListSeatChecked().size
        if (countSeat <= 0) {
            Toast.makeText(this, getString(R.string.msg_count_seat), Toast.LENGTH_SHORT).show()
            return
        }
        setListSeatUpdate()
        showDialogConfirmBooking()
    }

    private fun setListSeatUpdate() {
        for (seatChecked in getListSeatChecked()) {
            getSeatFirebaseFromId(seatChecked.roomId,
                seatChecked.timeId, seatChecked.id)?.isSelected = true
        }
    }

    private fun showDialogConfirmBooking() {
        mDialog = Dialog(this)
        mDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mDialog!!.setContentView(R.layout.layout_dialog_confirm_booking)
        val window = mDialog!!.window
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialog!!.setCancelable(false)

        // Get view
        val tvNameMovie = mDialog!!.findViewById<TextView>(R.id.tv_name_movie)
        val tvDateMovie = mDialog!!.findViewById<TextView>(R.id.tv_date_movie)
        val tvRoomMovie = mDialog!!.findViewById<TextView>(R.id.tv_room_movie)
        val tvTimeMovie = mDialog!!.findViewById<TextView>(R.id.tv_time_movie)
        val tvCountBooking = mDialog!!.findViewById<TextView>(R.id.tv_count_booking)
        val tvCountSeat = mDialog!!.findViewById<TextView>(R.id.tv_count_seat)
        val tvFoodDrink = mDialog!!.findViewById<TextView>(R.id.tv_food_drink)
        val tvPaymentMethod = mDialog!!.findViewById<TextView>(R.id.tv_payment_method)
        val tvTotalAmount = mDialog!!.findViewById<TextView>(R.id.tv_total_amount)
        val tvDialogCancel = mDialog!!.findViewById<TextView>(R.id.tv_dialog_cancel)
        val tvDialogOk = mDialog!!.findViewById<TextView>(R.id.tv_dialog_ok)

        // Set data
        val countView = getListSeatChecked().size
        mListFoodNeedUpdate = ArrayList(getListFoodSelected())
        tvNameMovie.text = mMovie?.name
        tvDateMovie.text = mMovie?.date
        tvRoomMovie.text = getTitleRoomSelected()
        tvTimeMovie.text = getTitleTimeSelected()
        tvCountBooking.text = countView.toString()
        tvCountSeat.text = getStringSeatChecked()
        tvFoodDrink.text = getStringFoodAndDrink()
        tvPaymentMethod.text = mPaymentMethodSelected?.name
        val strTotalAmount = getTotalAmount().toString() + ConstantKey.UNIT_CURRENCY
        tvTotalAmount.text = strTotalAmount

        // Set listener
        tvDialogCancel.setOnClickListener(object : IOnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                mDialog!!.dismiss()
            }
        })
        tvDialogOk.setOnClickListener(object : IOnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                val id = System.currentTimeMillis()
                mBookingHistory = BookingHistory(id, mMovie!!.id, mMovie?.name,
                    mMovie?.date, getTitleRoomSelected(), getTitleTimeSelected(),
                    tvCountBooking.text.toString(), getStringSeatChecked(),
                    getStringFoodAndDrink(), mPaymentMethodSelected?.name,
                    getTotalAmount(), DataStoreManager.getUser()?.email, false)
                if (ConstantKey.PAYMENT_CASH == mPaymentMethodSelected?.type) {
                    sendRequestOrder()
                } else {
                    getPaymentPaypal(getTotalAmount())
                }
            }
        })
        mDialog!!.show()
    }

    private fun sendRequestOrder() {
        if (mMovie == null) {
            return
        }
        mMovie!!.booked = mMovie!!.booked + mBookingHistory?.count?.toInt()!!
        MyApplication[this@ConfirmBookingActivity].getMovieDatabaseReference()
            .child(mMovie?.id.toString()).setValue(mMovie) { _: DatabaseError?, _: DatabaseReference? ->
                MyApplication[this@ConfirmBookingActivity].getBookingDatabaseReference()
                    .child(mBookingHistory?.id.toString())
                    .setValue(mBookingHistory) { _: DatabaseError?, _: DatabaseReference? ->
                        updateQuantityFoodDrink()
                        if (mDialog != null) mDialog!!.dismiss()
                        finish()
                        Toast.makeText(this@ConfirmBookingActivity,
                            getString(R.string.msg_booking_movie_success), Toast.LENGTH_LONG).show()
                        GlobalFunction.hideSoftKeyboard(this@ConfirmBookingActivity)
                    }
            }
    }

    private fun updateQuantityFoodDrink() {
        if (mListFoodNeedUpdate == null || mListFoodNeedUpdate!!.isEmpty()) {
            return
        }
        for (food in mListFoodNeedUpdate!!) {
            changeQuantity(food.id, food.count)
        }
    }

    private fun changeQuantity(foodId: Long, quantity: Int) {
        MyApplication[this].getQuantityDatabaseReference(foodId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentQuantity = snapshot.getValue(Int::class.java)
                    if (currentQuantity != null) {
                        val totalQuantity = currentQuantity - quantity
                        MyApplication[this@ConfirmBookingActivity]
                            .getQuantityDatabaseReference(foodId)
                            .removeEventListener(this)
                        MyApplication[this@ConfirmBookingActivity]
                            .getQuantityDatabaseReference(foodId)
                            .setValue(totalQuantity)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun getPaymentPaypal(price: Int) {
        //Creating a paypalpayment
        val payment = PayPalPayment(
            BigDecimal(price.toString()),
            PayPalConfig.PAYPAL_CURRENCY, PayPalConfig.PAYPAl_CONTENT_TEXT,
            PayPalPayment.PAYMENT_INTENT_SALE)

        //Creating Paypal Payment activity intent
        val intent = Intent(this, PaymentActivity::class.java)

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, PAYPAL_CONFIG)

        //Puting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment)

        //Starting the intent activity for result
        //the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE)
    }

    private fun getListSeatChecked() : List<SeatLocal> {
        val listSeatChecked: MutableList<SeatLocal> = java.util.ArrayList()
        if (mListSeats != null) {
            for (seat in mListSeats!!) {
                if (seat.isChecked) {
                    listSeatChecked.add(seat)
                }
            }
        }
        return listSeatChecked
    }
    private fun getListFoodSelected() : List<Food> {
        val listFoodSelected: MutableList<Food> = java.util.ArrayList()
        if (mListFood != null) {
            for (food in mListFood!!) {
                if (food.count > 0) {
                    listFoodSelected.add(food)
                }
            }
        }
        return listFoodSelected
    }
    private fun getStringFoodAndDrink() : String {
        var result = ""
        val listFoodSelected = getListFoodSelected()
        if (listFoodSelected.isEmpty()) {
            return "Không"
        }
        for (food in listFoodSelected) {
            if (StringUtil.isEmpty(result)) {
                result = (food.name + " (" + food.price
                        + ConstantKey.UNIT_CURRENCY + ")"
                        + " - Số lượng: " + food.count)
            } else {
                result = result + "\n" + (food.name + " (" + food.price
                        + ConstantKey.UNIT_CURRENCY + ")"
                        + " - Số lượng: " + food.count)
            }
        }
        return result
    }
    private fun getStringSeatChecked() : String {
        var result = ""
        val listSeatChecked = getListSeatChecked()
        for (seatLocal in listSeatChecked) {
            if (StringUtil.isEmpty(result)) {
                result += seatLocal.title
            } else {
                result = result + ", " + seatLocal.title
            }
        }
        return result
    }
    private fun getTotalAmount() : Int {
        if (mMovie == null) {
            return 0
        }
        var countBooking = 0
        try {
            countBooking = getListSeatChecked().size
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val priceMovie = countBooking * mMovie?.price!!
        var priceFoodDrink = 0
        val listFoodSelected = getListFoodSelected()
        if (listFoodSelected.isNotEmpty()) {
            for (food in listFoodSelected) {
                priceFoodDrink += food.price * food.count
            }
        }
        return priceMovie + priceFoodDrink
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PAYPAL_REQUEST_CODE) {
            var isPaymentSuccess = false

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == RESULT_OK) {
                //Getting the payment confirmation
                val confirm: PaymentConfirmation = data!!.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION)!!

                //if confirmation is not null
                try {
                    //Getting the payment details
                    val paymentDetails = confirm.toJSONObject().toString(4)
                    Log.e("Payment Result", paymentDetails)
                    val jsonDetails = JSONObject(paymentDetails)
                    val jsonResponse = jsonDetails.getJSONObject("response")
                    val strState = jsonResponse.getString("state")
                    Log.e("Payment State", strState)
                    if (PAYPAL_PAYMENT_STATUS_APPROVED == strState) {
                        isPaymentSuccess = true
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(this, getString(R.string.msg_payment_error), Toast.LENGTH_SHORT).show()
            }

            // Send result payment
            if (isPaymentSuccess) sendRequestOrder()
        }
    }

    companion object {
        const val PAYPAL_REQUEST_CODE = 199
        const val PAYPAL_PAYMENT_STATUS_APPROVED = "approved"

        //Paypal Configuration Object
        val PAYPAL_CONFIG: PayPalConfiguration = PayPalConfiguration()
            .environment(PayPalConfig.PAYPAL_ENVIRONMENT_DEV)
            .clientId(PayPalConfig.PAYPAL_CLIENT_ID_DEV)
            .acceptCreditCards(false)
    }
}
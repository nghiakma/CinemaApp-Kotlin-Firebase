package com.example.myapplication45.fragment.admin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication45.MyApplication
import com.example.myapplication45.R
import com.example.myapplication45.adapter.BookingHistoryAdapter
import com.example.myapplication45.constant.GlobalFunction.hideSoftKeyboard
import com.example.myapplication45.databinding.FragmentAdminBookingBinding
import com.example.myapplication45.event.ResultQrCodeEvent
import com.example.myapplication45.listener.IOnSingleClickListener
import com.example.myapplication45.model.BookingHistory
import com.example.myapplication45.util.DateTimeUtils.convertDateToTimeStamp
import com.example.myapplication45.util.DateTimeUtils.getLongCurrentTimeStamp
import com.example.myapplication45.util.StringUtil.isEmpty
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.zxing.integration.android.IntentIntegrator
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.ArrayList


class AdminBookingFragment : Fragment() {

    private var mFragmentAdminBookingBinding: FragmentAdminBookingBinding? = null
    private var mListBookingHistory: MutableList<BookingHistory>? = null
    private var mBookingHistoryAdapter: BookingHistoryAdapter? = null
    private var mIsUsedChecked = false
    private var mKeyWord = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mFragmentAdminBookingBinding = FragmentAdminBookingBinding.inflate(inflater, container, false)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
        initListener()
        listBookingHistory
        return mFragmentAdminBookingBinding!!.root
    }

    private fun initListener() {
        mFragmentAdminBookingBinding!!.imgSearch.setOnClickListener { searchBooking() }
        mFragmentAdminBookingBinding!!.chbBookingUsed.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            mIsUsedChecked = isChecked
            listBookingHistory
        }
        mFragmentAdminBookingBinding!!.edtSearchId.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchBooking()
                return@setOnEditorActionListener true
            }
            false
        }
        mFragmentAdminBookingBinding!!.edtSearchId.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val strKey = s.toString().trim { it <= ' ' }
                if (strKey == "" || strKey.isEmpty()) {
                    mKeyWord = ""
                    listBookingHistory
                }
            }
        })
        mFragmentAdminBookingBinding!!.imgScanQr.setOnClickListener(object : IOnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                clickOpenScanQRCode()
            }
        })
    }

    private fun searchBooking() {
        mKeyWord = mFragmentAdminBookingBinding!!.edtSearchId.text.toString().trim { it <= ' ' }
        listBookingHistory
        hideSoftKeyboard(activity)
    }

    val listBookingHistory: Unit
        get() {
            if (activity == null) {
                return
            }
            MyApplication[activity].getBookingDatabaseReference().addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (mListBookingHistory != null) {
                        mListBookingHistory!!.clear()
                    } else {
                        mListBookingHistory = ArrayList()
                    }
                    for (dataSnapshot in snapshot.children) {
                        val bookingHistory = dataSnapshot.getValue(BookingHistory::class.java)
                        if (bookingHistory != null) {
                            val isExpire = convertDateToTimeStamp(bookingHistory.date) < getLongCurrentTimeStamp()
                            if (mIsUsedChecked) {
                                if (isExpire || bookingHistory.isUsed) {
                                    if (isEmpty(mKeyWord)) {
                                        mListBookingHistory!!.add(0, bookingHistory)
                                    } else {
                                        if (bookingHistory.id.toString().contains(mKeyWord)) {
                                            mListBookingHistory!!.add(0, bookingHistory)
                                        }
                                    }
                                }
                            } else {
                                if (!isExpire && !bookingHistory.isUsed) {
                                    if (isEmpty(mKeyWord)) {
                                        mListBookingHistory!!.add(0, bookingHistory)
                                    } else {
                                        if (bookingHistory.id.toString().contains(mKeyWord)) {
                                            mListBookingHistory!!.add(0, bookingHistory)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    displayListBookingHistory()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

    private fun displayListBookingHistory() {
        if (activity == null) {
            return
        }
        val linearLayoutManager = LinearLayoutManager(activity)
        mFragmentAdminBookingBinding!!.rcvBookingHistory.layoutManager = linearLayoutManager
        mBookingHistoryAdapter = BookingHistoryAdapter(activity, true,
            mListBookingHistory, null, object : BookingHistoryAdapter.IClickConfirmListener {
                override fun onClickConfirmBooking(id: String) {
                    updateStatusBooking(id)
                }
            })
        mFragmentAdminBookingBinding!!.rcvBookingHistory.adapter = mBookingHistoryAdapter
    }

    private fun updateStatusBooking(id: String) {
        if (activity == null) {
            return
        }
        MyApplication[activity].getBookingDatabaseReference().child(id).child("used")
            .setValue(true) { _, _ -> Toast.makeText(activity, getString(R.string.msg_confirm_booking_success), Toast.LENGTH_SHORT).show() }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mBookingHistoryAdapter != null) mBookingHistoryAdapter!!.release()
    }

    private fun clickOpenScanQRCode() {
        val intentIntegrator = IntentIntegrator(activity)
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        intentIntegrator.setPrompt("Quét mã order vé xem phim")
        intentIntegrator.setCameraId(0)
        intentIntegrator.setOrientationLocked(true)
        intentIntegrator.initiateScan()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ResultQrCodeEvent?) {
        if (event != null) {
            mFragmentAdminBookingBinding!!.edtSearchId.setText(event.result)
            mKeyWord = event.result
            listBookingHistory
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }
}
package com.github.teresuki.timelineview.sample

//import com.github.vipulasri.timelineview.sample.example.ExampleActivity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.widget.Toolbar
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.teresuki.timelineview.TimelineView
import com.github.teresuki.timelineview.sample.extentions.dpToPx
import com.github.teresuki.timelineview.sample.extentions.getColorCompat
import com.github.teresuki.timelineview.sample.extentions.setGone
import com.github.teresuki.timelineview.sample.extentions.setVisible
import com.github.teresuki.timelineview.sample.model.OrderStatus
import com.github.teresuki.timelineview.sample.model.Orientation
import com.github.teresuki.timelineview.sample.model.TimeLineModel
import com.github.teresuki.timelineview.sample.model.TimelineAttributes
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Vipul Asri on 07-06-2016.
 * Modified by Teresuki Kitsune on 22-04-2021
 */

//REQUEST CODE
val NEW_TASK_REQUEST = 1
val EDIT_TASK_REQUEST = 2

//REQUEST CODE


class MainActivity : BaseActivity() {


    var mDataList = ArrayList<TimeLineModel>()
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAttributes: TimelineAttributes


    //Variables used to receive Data from Add Task and Edit Task
    var receivedTaskDes: String = ""
    var receivedTaskTime: String = ""
    var receivedTaskEditPos: Int = 0

    //


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWithoutInject(R.layout.activity_main)

        //Navigation Menu
        val myToolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_timeline)
        setSupportActionBar(myToolbar)

        //Navigation Menu

        //Add Task Button
        var addTaskButton = findViewById<Button>(R.id.buttonNewTask)
        addTaskButton.setOnClickListener {
            val intentNewTask = Intent(this, AddTaskActivity::class.java)
            startActivityForResult(intentNewTask, NEW_TASK_REQUEST)
        }


        //default values
        mAttributes = TimelineAttributes(
                markerSize = dpToPx(20f),
                markerColor = getColorCompat(R.color.orange1),
                markerInCenter = true,
                markerLeftPadding = dpToPx(0f),
                markerTopPadding = dpToPx(0f),
                markerRightPadding = dpToPx(0f),
                markerBottomPadding = dpToPx(0f),
                linePadding = dpToPx(2f),
                startLineColor = getColorCompat(R.color.orange2),
                endLineColor = getColorCompat(R.color.orange2),
                lineStyle = TimelineView.LineStyle.NORMAL,
                lineWidth = dpToPx(2f),
                lineDashWidth = dpToPx(4f),
                lineDashGap = dpToPx(2f)
        )


        //Load the Tasks from the Previous Activity
        loadData()


        // Old initialization of Example Tasks
//        setDataListItems()

        //The visual implementation of the Task so you can see them as List form.
        initRecyclerView()


        //Options
        fab_options.setOnClickListener {
            TimelineAttributesBottomSheet.showDialog(supportFragmentManager, mAttributes, object : TimelineAttributesBottomSheet.Callbacks {
                override fun onAttributesChanged(attributes: TimelineAttributes) {
                    mAttributes = attributes
                    initAdapter()
                }
            })
        }

        mAttributes.onOrientationChanged = { oldValue, newValue ->
            if (oldValue != newValue) initRecyclerView()
        }

        mAttributes.orientation = Orientation.VERTICAL

    }




    override fun onPause() {
        super.onPause()
        saveData()
    }

    fun saveData() {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(mDataList)
        editor.putString("Task List", json)
        editor.apply()

    }

    fun loadData() {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences("shared preferences", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("Task List", null)
        val type = object : TypeToken<ArrayList<TimeLineModel?>?>() {}.type
        if (json != null) {
            mDataList = gson.fromJson(json, type)
        }


        if (mDataList == null) {
            val mDataList: ArrayList<TimeLineModel>
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun startAlarm(c: Calendar, taskTitle: String, taskText: String, requestCode: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlertReceiver::class.java)
        intent.putExtra("TaskTitle", taskTitle);
        intent.putExtra("TaskText", taskText);

        val pendingIntent = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)
        Toast.makeText(this, "Alarm set successfully", Toast.LENGTH_SHORT).show()
    }

    private fun cancelAlarm(requestCode: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlertReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, 0)
        alarmManager.cancel(pendingIntent)
        Toast.makeText(this,"Alarm Cancelled", Toast.LENGTH_SHORT).show();
    }


    //Used to Generate Distinct Request Code for Alarm for Tasks
    private fun generateTimeBasedInt() : Int {
        return (System.currentTimeMillis()/1000).toInt();
    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //TEST
        var myCalendar = Calendar.getInstance();

        if (requestCode == NEW_TASK_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    receivedTaskDes = data.getStringExtra("taskDes")
                    receivedTaskTime = data.getStringExtra("taskTime")


                    var newTask = TimeLineModel(receivedTaskDes, receivedTaskTime, generateTimeBasedInt(), OrderStatus.ACTIVE);

                    mDataList.add(newTaskAddPosition(receivedTaskTime), newTask)

                    //Add Task to Notification List
                    //Check for null time
                    if(newTask.date != "Start Time")
                    {
                        myCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(receivedTaskTime.substring(0,2)));
                        myCalendar.set(Calendar.MINUTE, Integer.parseInt(receivedTaskTime.substring(3,5)));
                        myCalendar.set(Calendar.SECOND, 0);
                        startAlarm(myCalendar, newTask.message, newTask.date, newTask.alarmRequestCode)
                    }

                    recyclerView.adapter = TimeLineAdapter(mDataList, mAttributes)
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                recyclerView.adapter = TimeLineAdapter(mDataList, mAttributes)
            }


        } else if (requestCode == EDIT_TASK_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    receivedTaskDes = data.getStringExtra("taskDesEdit")
                    receivedTaskTime = data.getStringExtra("taskTimeEdit")
                    receivedTaskEditPos = data.getIntExtra("taskPosEdit", 0)

                    //Remove the old Task and its Alarm
                    var oldTask = mDataList.elementAt(receivedTaskEditPos)
                    cancelAlarm(oldTask.alarmRequestCode)
                    mDataList.removeAt(receivedTaskEditPos)

                    var newTask = TimeLineModel(receivedTaskDes, receivedTaskTime, generateTimeBasedInt(), OrderStatus.ACTIVE)

                    mDataList.add(newTaskAddPosition(receivedTaskTime) ,newTask)

                    //Add Task to Notification List
                    //Check for null time
                    if(newTask.date != "Start Time")
                    {
                        myCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(receivedTaskTime.substring(0,2)));
                        myCalendar.set(Calendar.MINUTE, Integer.parseInt(receivedTaskTime.substring(3,5)));
                        myCalendar.set(Calendar.SECOND, 0);
                        startAlarm(myCalendar, newTask.message, newTask.date, newTask.alarmRequestCode)
                    }

                    recyclerView.adapter = TimeLineAdapter(mDataList, mAttributes)
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                if (data != null) {
                    receivedTaskEditPos = data.getIntExtra("taskPosEdit", 0)

                    //Remove the old Task and its Alarm
                    var oldTask = mDataList.elementAt(receivedTaskEditPos)
                    cancelAlarm(oldTask.alarmRequestCode)
                    mDataList.removeAt(receivedTaskEditPos)

                    recyclerView.adapter = TimeLineAdapter(mDataList, mAttributes)
                    Toast.makeText(applicationContext, "Deleted a Task", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    //Insertion Sort
    fun newTaskAddPosition(newTaskTime : String) : Int {
        var oldTaskMinute = ""
        var newTaskMinute : String
        var insertIndexOfTask : Int = 0
        newTaskMinute = newTaskTime.substring(0, 2) + newTaskTime.substring(3, 5)
        val n: Int = mDataList.size
        for (i in 0 until n)
        {
            oldTaskMinute = mDataList[i].date[0].toString() + mDataList[i].date[1].toString() + mDataList[i].date[3].toString() + mDataList[i].date[4].toString()
            if (newTaskMinute.compareTo(oldTaskMinute) >= 0)
            {
              insertIndexOfTask = i+1
            }
        }// end i


         return insertIndexOfTask

    }

    private fun setDataListItems() {

        /*  mDataList.add(TimeLineModel("Item successfully delivered", "", OrderStatus.INACTIVE))
          mDataList.add(TimeLineModel("Testing Item", "2017-02-13 12:00", OrderStatus.ACTIVE))
          mDataList.add(TimeLineModel("Courier is out to delivery your order", "2017-02-12 08:00", OrderStatus.ACTIVE))
          mDataList.add(TimeLineModel("Item has reached courier facility at New Delhi", "2017-02-11 21:00", OrderStatus.COMPLETED))
          mDataList.add(TimeLineModel("Item has been given to the courier", "2017-02-11 18:00", OrderStatus.COMPLETED))
          mDataList.add(TimeLineModel("Item is packed and will dispatch soon", "2017-02-11 09:30", OrderStatus.COMPLETED))
          mDataList.add(TimeLineModel("Order is being readied for dispatch", "2017-02-11 08:00", OrderStatus.COMPLETED))
          mDataList.add(TimeLineModel("Order processing initiated", "2017-02-10 15:00", OrderStatus.COMPLETED))
          mDataList.add(TimeLineModel("Order confirmed by seller", "2017-02-10 14:30", OrderStatus.COMPLETED))
          mDataList.add(TimeLineModel("Order placed successfully", "2017-02-10 14:00", OrderStatus.COMPLETED))*/
    }

    private fun initRecyclerView() {
        initAdapter()
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            @SuppressLint("LongLogTag")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView.getChildAt(0).top < 0) dropshadow.setVisible() else dropshadow.setGone()
            }
        })
    }

    private fun initAdapter() {
        mLayoutManager = if (mAttributes.orientation == Orientation.HORIZONTAL) {
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        } else {
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        }

        recyclerView.apply {
            layoutManager = mLayoutManager
            adapter = TimeLineAdapter(mDataList, mAttributes)
        }
    }


}



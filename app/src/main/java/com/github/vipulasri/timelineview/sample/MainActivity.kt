package com.github.vipulasri.timelineview.sample

//import com.github.vipulasri.timelineview.sample.example.ExampleActivity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.vipulasri.timelineview.TimelineView
import com.github.vipulasri.timelineview.sample.extentions.dpToPx
import com.github.vipulasri.timelineview.sample.extentions.getColorCompat
import com.github.vipulasri.timelineview.sample.extentions.setGone
import com.github.vipulasri.timelineview.sample.extentions.setVisible
import com.github.vipulasri.timelineview.sample.model.OrderStatus
import com.github.vipulasri.timelineview.sample.model.Orientation
import com.github.vipulasri.timelineview.sample.model.TimeLineModel
import com.github.vipulasri.timelineview.sample.model.TimelineAttributes
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*


/**
 * Created by Vipul Asri on 07-06-2016.
 */

//REQUEST CODE
val NEW_TASK_REQUEST = 1
val EDIT_TASK_REQUEST = 2

//REQUEST CODE




class MainActivity : BaseActivity() {

    //TESTING


    //TESTING

    private var mDataList = ArrayList<TimeLineModel>()
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAttributes: TimelineAttributes




    var receivedTaskDes: String = ""
    var receivedTaskTime: String = ""
    var receivedTaskEditPos: Int = 0

    //


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentViewWithoutInject(R.layout.activity_main)



        // Yellow Fox

        //Loading Data


        //Loading Data


        var addTaskButton = findViewById<Button>(R.id.buttonNewTask)
        addTaskButton.setOnClickListener {
            //startActivity(Intent(this, AddTaskActivity::class.java))

            val intentNewTask = Intent(this, AddTaskActivity::class.java)
            startActivityForResult(intentNewTask, NEW_TASK_REQUEST)
        }

        // calling method to load data
        // from shared prefs.


        // Yellow Fox


        //default values
        mAttributes = TimelineAttributes(
                markerSize = dpToPx(20f),
                markerColor = getColorCompat(R.color.material_grey_500),
                markerInCenter = true,
                markerLeftPadding = dpToPx(0f),
                markerTopPadding = dpToPx(0f),
                markerRightPadding = dpToPx(0f),
                markerBottomPadding = dpToPx(0f),
                linePadding = dpToPx(2f),
                startLineColor = getColorCompat(R.color.colorAccent),
                endLineColor = getColorCompat(R.color.colorAccent),
                lineStyle = TimelineView.LineStyle.NORMAL,
                lineWidth = dpToPx(2f),
                lineDashWidth = dpToPx(4f),
                lineDashGap = dpToPx(2f)
        )

        setDataListItems()
        initRecyclerView()


        //action_example_activity.setOnClickListener { startActivity(Intent(this, ExampleActivity::class.java)) }

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


    //Test

    override fun onStart() {
        super.onStart()

        val sharedPreferences: SharedPreferences = this.getSharedPreferences("TIMELINE", Context.MODE_PRIVATE);

        fun loadSavedData()
        {
            val loadedItemSize : Int;
            loadedItemSize = sharedPreferences.getInt("SAVED_SIZE", 0)
            for(i in 0..(loadedItemSize-1) )
            {
                mDataList[i].date = sharedPreferences.getString("SAVED_DATE", "")
                mDataList[i].message = sharedPreferences.getString("SAVED_MESSAGE","")
                mDataList[i].status = OrderStatus.ACTIVE
            }
        }
        loadSavedData()
        initRecyclerView()



    }

    override fun onPause() {
        super.onPause()

        val sharedPreferences: SharedPreferences = this.getSharedPreferences("TIMELINE", Context.MODE_PRIVATE);
        val editor = sharedPreferences.edit()



        //STORE DATA
        fun setOneItem(key1: String?, value1: String?, key2: String?, value2: String?) {
            editor.putString(key1, value1);
            editor.putString(key2, value2);
            editor.commit()
        }
        fun setItemSize(key1: String?, value1: Int?)
        {
            if (value1 != null) {
                editor.putInt(key1, value1)
            };
        }
        fun setAllItem(key: String?, list: List<TimeLineModel>?) {
            val gson = Gson()
            val json = gson.toJson(list)
            for(i in this.mDataList.indices)
            {
                setOneItem("SAVED_DATE", mDataList[i].date, "SAVED_MESSAGE", mDataList[i].message)
                Toast.makeText(this,"SAVED ITEM #" +(i+1) , LENGTH_SHORT);

                if(i == (mDataList.size-1) ) setItemSize("SAVED_SIZE", mDataList.size);
            }
        }

        //STORE DATA



        //save All Data when app is terminated
        setAllItem("TimeLine", mDataList);

    }






    var newTaskReceived = false
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == NEW_TASK_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    receivedTaskDes = data.getStringExtra("taskDes")
                    receivedTaskTime = data.getStringExtra("taskTime")
                    newTaskReceived = true
                    mDataList.add(TimeLineModel(receivedTaskDes, receivedTaskTime, OrderStatus.ACTIVE))
                    sortingDataListItems()
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
                    newTaskReceived = true
                    mDataList.removeAt(receivedTaskEditPos)
                    mDataList.add(TimeLineModel(receivedTaskDes, receivedTaskTime, OrderStatus.ACTIVE))
                    sortingDataListItems()
                    recyclerView.adapter = TimeLineAdapter(mDataList, mAttributes)
                }
            }
            else if(resultCode == Activity.RESULT_CANCELED){
                if(data != null){
                    receivedTaskEditPos = data.getIntExtra("taskPosEdit", 0);
                    newTaskReceived = true
                    mDataList.removeAt(receivedTaskEditPos)
                    sortingDataListItems()
                    recyclerView.adapter = TimeLineAdapter(mDataList, mAttributes)
                    Toast.makeText(applicationContext, "Deleted a Task", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    fun sortingDataListItems() {
        var temp = ArrayList<TimeLineModel>()
        var taskMinute1 = ""
        var taskMinute2 = ""
        val n: Int = mDataList.size
        for (i in 0 until n - 1) {
            for (j in i + 1 until n) {
                taskMinute1 = mDataList[i].date[0].toString() + mDataList[i].date[1].toString() + mDataList[i].date[3].toString() + mDataList[i].date[4].toString()
                taskMinute2 = mDataList[j].date[0].toString() + mDataList[j].date[1].toString() + mDataList[j].date[3].toString() + mDataList[j].date[4].toString()

                if (taskMinute1.compareTo(taskMinute2) > 0) {
                    // swap arr[j+1] and arr[j]o0
                    temp.add(mDataList[j])
                    temp.add(mDataList[i])
                    mDataList[i] = temp[0]
                    mDataList[j] = temp[1]
                    temp.removeAt(1)
                    temp.removeAt(0)
                    taskMinute1 = ""
                    taskMinute2 = "" //Safety Measure
                }

            }// end j

        }// end i
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



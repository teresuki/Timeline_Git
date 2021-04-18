package com.github.vipulasri.timelineview.sample

import android.app.Activity
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView

import com.github.vipulasri.timelineview.sample.model.OrderStatus
import com.github.vipulasri.timelineview.sample.model.Orientation
import com.github.vipulasri.timelineview.sample.model.TimeLineModel
import com.github.vipulasri.timelineview.sample.utils.VectorDrawableUtils
import com.github.vipulasri.timelineview.TimelineView
import com.github.vipulasri.timelineview.sample.extentions.formatDateTime
import com.github.vipulasri.timelineview.sample.extentions.setGone
import com.github.vipulasri.timelineview.sample.extentions.setVisible
import com.github.vipulasri.timelineview.sample.model.TimelineAttributes
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_task.view.*
import kotlinx.android.synthetic.main.item_timeline.view.*

/**
 * Created by Vipul Asri on 05-12-2015.
 */

class TimeLineAdapter(private val mFeedList: List<TimeLineModel>, private var mAttributes: TimelineAttributes) : RecyclerView.Adapter<TimeLineAdapter.TimeLineViewHolder>() {

    private lateinit var mLayoutInflater: LayoutInflater

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {

        if (!::mLayoutInflater.isInitialized) {
            mLayoutInflater = LayoutInflater.from(parent.context)
        }

        val view = if (mAttributes.orientation == Orientation.HORIZONTAL) {
            mLayoutInflater.inflate(R.layout.item_timeline_horizontal, parent, false)
        } else {
            mLayoutInflater.inflate(R.layout.item_timeline, parent, false)
        }
        return TimeLineViewHolder(view, viewType)
    }

    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {

        val timeLineModel = mFeedList[position]

        when {
            timeLineModel.status == OrderStatus.INACTIVE -> {
                holder.timeline.marker = VectorDrawableUtils.getDrawable(holder.itemView.context, R.drawable.ic_marker_inactive, mAttributes.markerColor)
            }
            timeLineModel.status == OrderStatus.ACTIVE -> {
                holder.timeline.marker = VectorDrawableUtils.getDrawable(holder.itemView.context, R.drawable.ic_marker_active, mAttributes.markerColor)
            }
            else -> {
                holder.timeline.setMarker(ContextCompat.getDrawable(holder.itemView.context, R.drawable.ic_marker), mAttributes.markerColor)
            }
        }

        if (timeLineModel.date.isNotEmpty()) {
            holder.date.setVisible()
            holder.date.text = timeLineModel.date
        } else
            holder.date.setGone()

        holder.message.text = timeLineModel.message

        //Teresuki

        //REQUEST CODE
        val EDIT_TASK_REQUEST = 2
        //REQUEST CODE

        holder.itemView.setOnClickListener {
            val position: Int = holder.adapterPosition
            val context = holder.itemView.context as Activity
            Toast.makeText(context, "You clicked on item #" + (position + 1), Toast.LENGTH_SHORT).show()

            val intentEditText = Intent(context, EditTaskActivity::class.java)


            intentEditText.putExtra("curTaskTime", mFeedList[position].date)
            intentEditText.putExtra("curTaskDes", mFeedList[position].message)
            intentEditText.putExtra("curTaskPos", position)
            context.startActivityForResult(intentEditText, EDIT_TASK_REQUEST);

        }

        //Teresuki
    }

    override fun getItemCount() = mFeedList.size

    inner class TimeLineViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {

        val date = itemView.text_timeline_date
        val message = itemView.text_timeline_title
        val timeline = itemView.timeline


        init {


            timeline.initLine(viewType)
            timeline.markerSize = mAttributes.markerSize
            timeline.setMarkerColor(mAttributes.markerColor)
            timeline.isMarkerInCenter = mAttributes.markerInCenter
            timeline.markerPaddingLeft = mAttributes.markerLeftPadding
            timeline.markerPaddingTop = mAttributes.markerTopPadding
            timeline.markerPaddingRight = mAttributes.markerRightPadding
            timeline.markerPaddingBottom = mAttributes.markerBottomPadding
            timeline.linePadding = mAttributes.linePadding

            timeline.lineWidth = mAttributes.lineWidth
            timeline.setStartLineColor(mAttributes.startLineColor, viewType)
            timeline.setEndLineColor(mAttributes.endLineColor, viewType)
            timeline.lineStyle = mAttributes.lineStyle
            timeline.lineStyleDashLength = mAttributes.lineDashWidth
            timeline.lineStyleDashGap = mAttributes.lineDashGap
        }
    }


}



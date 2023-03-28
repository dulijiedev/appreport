package com.roobo.appreport

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.roobo.appreport.adapter.SubjectAdapter
import com.roobo.appreport.data.LastSelectEntity
import com.roobo.appreport.databinding.SwitchCourseLayoutBinding
import com.roobo.appreport.utils.ScreenUtil
import com.roobo.appreport.utils.UIUtils

class CourseDialog(
    context: Context,
    val list: MutableList<LastSelectEntity>,
    var currentSelectEntity: LastSelectEntity?,
    val callback: (LastSelectEntity?) -> Unit
) : BottomSheetDialog(context,R.style.BottomSheetDialog) {

    private var switchAdapter: SubjectAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        val switchBinding = DataBindingUtil.inflate<SwitchCourseLayoutBinding>(
            layoutInflater,
            R.layout.switch_course_layout,
            null,
            false
        )
        setContentView(switchBinding.root)
        switchBinding.listCourse.layoutManager = LinearLayoutManager(context)
        switchAdapter = SubjectAdapter(list, onItemClick = { data, index ->
            val old = switchAdapter?.selectIndex ?: 0
            switchAdapter?.selectIndex = index
            switchAdapter?.notifyItemChanged(old)
            switchAdapter?.notifyItemChanged(index)
        })
        val selectIndex = switchAdapter?.list?.indexOf(currentSelectEntity) ?: 0
        switchAdapter?.selectIndex = selectIndex

        switchBinding.listCourse.adapter = switchAdapter
        switchBinding.tvSave.setOnClickListener {
            var entity = currentSelectEntity
            val index = switchAdapter?.selectIndex ?: 0
            if (index >= 0 && index < list.size) {
                entity = list[index]
            }
//            currentSelectEntity?.subjectId?.toInt()?.let { it1 -> getKnowDataRemote(it1) }
//            mBinding.tvTips.text = currentSelectEntity?.subjectName ?: "--"
            callback(entity)
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        behavior.setPeekHeight(UIUtils.dip2px(332f))

    }

    override fun show() {
        super.show()
        val attr = window?.attributes
        attr?.gravity = Gravity.BOTTOM
        attr?.height = UIUtils.dip2px(332f)
        attr?.width = ScreenUtil.getWith(context)
        window?.attributes = attr
    }

}
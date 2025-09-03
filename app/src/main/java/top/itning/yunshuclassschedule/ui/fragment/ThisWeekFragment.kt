package top.itning.yunshuclassschedule.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.google.android.material.color.MaterialColors
import com.google.android.material.R as M3
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import top.itning.yunshuclassschedule.LocaleHelper
import top.itning.yunshuclassschedule.common.App
import top.itning.yunshuclassschedule.common.ConstantPool
import top.itning.yunshuclassschedule.databinding.FragmentThisWeekBinding
import top.itning.yunshuclassschedule.entity.EventEntity
import top.itning.yunshuclassschedule.ui.adapter.ClassScheduleItemLongClickListener
import top.itning.yunshuclassschedule.ui.fragment.setting.SettingsFragment
import top.itning.yunshuclassschedule.util.ClassScheduleUtils
import top.itning.yunshuclassschedule.util.ClassScheduleUtils.COPY_LIST

class ThisWeekFragment : Fragment() {

    private var _binding: FragmentThisWeekBinding? = null
    private val binding get() = _binding!!

    private lateinit var clickListener: ClassScheduleItemLongClickListener

    override fun onAttach(context: Context) {
        super.onAttach(LocaleHelper.applyAppLocale(context))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThisWeekBinding.inflate(inflater, container, false)

        // Remove picture background; use theme surface instead
        val surface = MaterialColors.getColor(requireContext(), M3.attr.colorSurface, 0)
        binding.root.setBackgroundColor(surface)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val nowWeekNum = (PreferenceManager.getDefaultSharedPreferences(context)
            .getString(SettingsFragment.NOW_WEEK_NUM, "1")!!.toInt() - 1).toString()

        val daoSession = (requireActivity().application as App).daoSession
        val classScheduleList = daoSession.classScheduleDao.loadAll()
            .filter { ClassScheduleUtils.isThisWeekOfClassSchedule(it, nowWeekNum) }
            .toMutableList()

        clickListener = ClassScheduleItemLongClickListener(requireActivity(), classScheduleList, COPY_LIST)

        // Use binding instead of synthetics
        ClassScheduleUtils.loadingView(
            classScheduleList,
            binding.scheduleGridlayout,
            binding.glHeader,
            clickListener,
            requireActivity()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(eventEntity: EventEntity) {
        when (eventEntity.id) {
            ConstantPool.Int.REFRESH_WEEK_FRAGMENT_DATA -> {
                val nowWeekNum = (PreferenceManager.getDefaultSharedPreferences(context)
                    .getString(SettingsFragment.NOW_WEEK_NUM, "1")!!.toInt() - 1).toString()
                val daoSession = (requireActivity().application as App).daoSession
                val classScheduleList = daoSession.classScheduleDao.loadAll()
                    .filter { ClassScheduleUtils.isThisWeekOfClassSchedule(it, nowWeekNum) }
                    .toMutableList()
                clickListener = ClassScheduleItemLongClickListener(requireActivity(), classScheduleList, COPY_LIST)
                ClassScheduleUtils.loadingView(
                    classScheduleList,
                    binding.scheduleGridlayout,
                    binding.glHeader,
                    clickListener,
                    requireActivity()
                )
            }

            ConstantPool.Int.APP_COLOR_CHANGE -> {
                clickListener.updateBtnBackgroundTintList()
            }

            ConstantPool.Int.CLASS_WEEK_CHANGE -> {
                val nowWeekNum = (eventEntity.msg!!.toInt() - 1).toString()
                val daoSession = (requireActivity().application as App).daoSession
                val classScheduleList = daoSession.classScheduleDao.loadAll()
                    .filter { ClassScheduleUtils.isThisWeekOfClassSchedule(it, nowWeekNum) }
                    .toMutableList()
                clickListener = ClassScheduleItemLongClickListener(requireActivity(), classScheduleList, COPY_LIST)
                ClassScheduleUtils.loadingView(
                    classScheduleList,
                    binding.scheduleGridlayout,
                    binding.glHeader,
                    clickListener,
                    requireActivity()
                )
            }

            else -> {
                // Not relevant for this fragment; ignore.
                // (Examples the IDE listed: DELAY_INTO_MAIN_ACTIVITY_TIME, HTTP_ERROR, etc.)
            }
        }
    }


    companion object {
        private const val TAG = "ThisWeekFragment"
    }
}

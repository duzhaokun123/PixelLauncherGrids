package io.github.duzhaokun123.pixellaunchergrids

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.util.Log
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.init.InitFields
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.getObject
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import com.github.kyuubiran.ezxhelper.utils.loadClass
import com.github.kyuubiran.ezxhelper.utils.paramCount
import com.ironz.unsafe.UnsafeAndroid
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage

class XposedInit : IXposedHookLoadPackage {
    companion object {
        const val TAG = "PixelLauncherGrids"
    }

    val unsafe = UnsafeAndroid()

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.google.android.apps.nexuslauncher") return
        EzXHelperInit.initHandleLoadPackage(lpparam)
        loadClass("com.android.launcher3.InvariantDeviceProfile")
            .findMethod { name == "parseAllGridOptions" }
            .hookAfter {
                EzXHelperInit.initAppContext(it.args[0] as Context)
                it.result = (it.result as List<*>).plus(get5x7GridOption())
            }
        loadClass("com.android.launcher3.InvariantDeviceProfile")
            .findMethod { name == "getPredefinedDeviceProfiles" }
            .hookBefore {
                if (it.args[1] != "pretty_normal") return@hookBefore
                EzXHelperInit.initAppContext(it.args[0] as Context)
                it.result = arrayListOf(get5x7DisplayOption())
            }
    }

    private fun get5x7GridOption(): Any {
        val gridOption = unsafe.allocateInstance(loadClass("com.android.launcher3.InvariantDeviceProfile\$GridOption"))
        // public final String name;
        gridOption.setField("name", "pretty_normal")
        // public final int numRows;
        gridOption.setField("numRows", 7)
        // public final int numColumns;
        gridOption.setField("numColumns", 5)
        // public final int numSearchContainerColumns;
        gridOption.setField("numSearchContainerColumns", 5)
        // public final int deviceCategory;
        gridOption.setField("deviceCategory", 0 /* DEVICE_CATEGORY_PHONE */)
        //
        // private final int numFolderRows;
        gridOption.setField("numFolderRows", 4)
        // private final int numFolderColumns;
        gridOption.setField("numFolderColumns", 4)
        // private final @StyleRes int folderStyle;
        gridOption.setField("folderStyle", getResByName("Folder5x5Style", "style"))
        // private final @StyleRes int cellStyle;
        gridOption.setField("cellStyle", getResByName("CellStyleDefault", "style"))
        //
        // private final @StyleRes int allAppsStyle;
        gridOption.setField("allAppsStyle", getResByName("AllAppsStyleDefault", "style"))
        // private final int numAllAppsColumns;
        gridOption.setField("numAllAppsColumns", 5)
        // private final int numDatabaseAllAppsColumns;
        gridOption.setField("numDatabaseAllAppsColumns", 7)
        // private final int numHotseatIcons;
        gridOption.setField("numHotseatIcons", 5)
        // private final int numDatabaseHotseatIcons;
        gridOption.setField("numDatabaseHotseatIcons", 7)
        //
        // private final int[] hotseatColumnSpan = new int[COUNT_SIZES];
        gridOption.setField("hotseatColumnSpan", intArrayOf(5, 5, 5, 5))
        //
        // private final boolean[] inlineQsb = new boolean[COUNT_SIZES];
        gridOption.setField("inlineQsb", booleanArrayOf(false, false, false, false))
        //
        // private @DimenRes int inlineNavButtonsEndSpacing;
        gridOption.setField("inlineNavButtonsEndSpacing", getResByName("taskbar_button_margin_default", "dimen"))
        // private final String dbFile;
        gridOption.setField("dbFile", "launcher_5_by_7.db")
        //
        // private final int defaultLayoutId;
        gridOption.setField("defaultLayoutId", getResByName("default_workspace_5x5", "xml"))
        // private final int demoModeLayoutId;
        gridOption.setField("demoModeLayoutId", getResByName("default_workspace_5x5", "xml"))
        //
        // private final boolean isScalable;
        gridOption.setField("isScalable", true)
        // private final int devicePaddingId;
        gridOption.setField("devicePaddingId", -1)
        return gridOption
    }

    private fun get5x7DisplayOption(): Any {
        val displayOption = unsafe.allocateInstance(loadClass("com.android.launcher3.InvariantDeviceProfile\$DisplayOption"))
        // public final GridOption grid;
        displayOption.setField("grid", get5x7GridOption())
        //
        // private final float minWidthDps;
        displayOption.setField("minWidthDps", 387.0F)
        // private final float minHeightDps;
        displayOption.setField("minHeightDps", 750.0F)
        // private final boolean canBeDefault;
        displayOption.setField("canBeDefault", false)
        //
        // private final PointF[] minCellSize = new PointF[COUNT_SIZES];
        displayOption.setField("minCellSize", arrayOf(PointF(61.0F, 108.0F), PointF(61.0F, 108.0F), PointF(61.0F, 108.0F), PointF(61.0F, 108.0F)))
        //
        // private final PointF[] borderSpaces = new PointF[COUNT_SIZES];
        displayOption.setField("borderSpaces", arrayOf(PointF(16.0F, 16.0F), PointF(16.0F, 16.0F), PointF(16.0F, 16.0F), PointF(16.0F, 16.0F)))
        // private final float[] horizontalMargin = new float[COUNT_SIZES];
        displayOption.setField("horizontalMargin", floatArrayOf(21.0F, 21.0F, 21.0F, 21.0F))
        // private final float[] hotseatBarBottomSpace = new float[COUNT_SIZES];
        val hotseat_bar_bottom_space_default = InitFields.appContext.resources.getFloat(getResByName("hotseat_bar_bottom_space_default", "dimen"))
        displayOption.setField("hotseatBarBottomSpace", floatArrayOf(
            hotseat_bar_bottom_space_default,
            hotseat_bar_bottom_space_default,
            hotseat_bar_bottom_space_default,
            hotseat_bar_bottom_space_default
        ))
        // private final float[] hotseatQsbSpace = new float[COUNT_SIZES];
        val hotseat_qsb_space_default = InitFields.appContext.resources.getFloat(getResByName("hotseat_qsb_space_default", "dimen"))
        displayOption.setField("hotseatQsbSpace", floatArrayOf(
            hotseat_qsb_space_default,
            hotseat_qsb_space_default,
            hotseat_qsb_space_default,
            hotseat_qsb_space_default
        ))
        //
        // private final float[] iconSizes = new float[COUNT_SIZES];
        displayOption.setField("iconSizes", floatArrayOf(56.0F, 49.0F, 56.0F, 56.0F))
        // private final float[] textSizes = new float[COUNT_SIZES];
        displayOption.setField("textSizes", floatArrayOf(12.0F, 12.0F, 12.0F, 12.0F))
        //
        // private final PointF[] allAppsCellSize = new PointF[COUNT_SIZES];
        displayOption.setField("allAppsCellSize", arrayOf(PointF(61.0F, 104.0F), PointF(61.0F, 104.0F), PointF(61.0F, 104.0F), PointF(61.0F, 104.0F)))
        // private final float[] allAppsIconSizes = new float[COUNT_SIZES];
        displayOption.setField("allAppsIconSizes", floatArrayOf(56.0F, 51.0F, 56.0F, 56.0F))
        // private final float[] allAppsIconTextSizes = new float[COUNT_SIZES];
        displayOption.setField("allAppsIconTextSizes", floatArrayOf(12.0F, 12.0F, 12.0F, 12.0F))
        // private final PointF[] allAppsBorderSpaces = new PointF[COUNT_SIZES];
        displayOption.setField("allAppsBorderSpaces", arrayOf(PointF(16.0F, 16.0F), PointF(16.0F, 16.0F), PointF(16.0F, 16.0F), PointF(16.0F, 16.0F)))
        //
        // private final float[] transientTaskbarIconSize = new float[COUNT_SIZES];
        val taskbar_icon_size = InitFields.appContext.resources.getFloat(getResByName("taskbar_icon_size", "dimen"))
        displayOption.setField("transientTaskbarIconSize", floatArrayOf(taskbar_icon_size, taskbar_icon_size, taskbar_icon_size, taskbar_icon_size))
        //
        // private final boolean[] startAlignTaskbar = new boolean[COUNT_SIZES];
        displayOption.setField("startAlignTaskbar", booleanArrayOf(false, false, false, false))
        return displayOption
    }
}

fun Any.setField(name: String, value: Any?) {
    this::class.java.getDeclaredField(name).apply {
        isAccessible = true
        set(this@setField, value)
    }
}

@SuppressLint("DiscouragedApi")
fun getResByName(name: String, defType: String, ctx: Context = InitFields.appContext): Int {
    return ctx.resources.getIdentifier(name, defType, ctx.packageName)
}

fun Any.fieldToString(): String {
    val sb = StringBuilder()
    this::class.java.declaredFields.forEach {
        it.isAccessible = true
        sb.append("${it.name} = ${it.get(this)}\n")
    }
    return sb.toString()
}
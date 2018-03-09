#指定代码的压缩级别
-optimizationpasses 5
 #预校验
-dontpreverify
 #混淆时是否记录日志
-verbose

#####################记录生成的日志数据,gradle build时在本项目根目录输出################
# apk 包内所有 class 的内部结构
-dump class_files.txt
# 未混淆的类和成员
-printseeds seeds.txt
# 列出从 apk 中删除的代码
-printusage unused.txt
# 混淆前后的映射
-printmapping mapping.txt
#####################记录生成的日志数据，gradle build时 在本项目根目录输出################

#保护注解
-keepattributes *Annotation*
#避免混淆泛型 如果混淆报错建议关掉
-keepattributes Signature

#-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
#-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver

#如果有引用v4包可以添加下面这行
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
#如果引用了v4或者v7包，可以忽略警告，因为用不到android.support
-dontwarn android.support.**
#忽略警告
-ignorewarning

# 保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context);
}
# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
# 保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
public static final android.os.Parcelable$Creator *;
}
# 保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable

##### 以下自定义 #################################################################

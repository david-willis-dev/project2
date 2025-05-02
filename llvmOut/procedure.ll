@.str1 = private unnamed_addr constant [8 x i8] c"Hello!\0A\00", align 1
define dso_local noundef i32 @main() #1 {
%2 = call i32 (ptr, ...) @printf(ptr noundef @.str1)

  ret i32 1 
}
 declare i32 @printf(ptr noundef, ...) #1
attributes #1 = { "frame-pointer"="all" "no-trapping-math"="true" "stack-protector-buffer-size"="8"  "tune-cpu"="generic" }
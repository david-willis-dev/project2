@.str3 = private unnamed_addr constant [3 x i8] c"2\0A\00", align 1
@.str2 = private unnamed_addr constant [14 x i8] c"You entered: \00", align 1
@.str1 = private unnamed_addr constant [19 x i8] c"Enter an integer: \00", align 1
define dso_local noundef i32 @main() #1 {
%num_0 = alloca i32, align 4
%2 = call i32 (ptr, ...) @printf(ptr noundef @.str1)
%3 = call i32 (ptr, ...) @printf(ptr noundef @.str2)
%4 = call i32 (ptr, ...) @printf(ptr noundef @.str3)

  ret i32 1 
}
 declare i32 @printf(ptr noundef, ...) #1
attributes #1 = { "frame-pointer"="all" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "tune-cpu"="generic" }
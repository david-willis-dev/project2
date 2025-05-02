@.str1 = private unnamed_addr constant [15 x i8] c"1 plus 4 is 5\0A\00", align 1
define dso_local noundef i32 @main() #1 {
%num1_0 = alloca i32, align 4
%num2_0 = alloca i32, align 4
%result_0 = alloca i32, align 4
store i32 1, ptr %num1_0, align 4
store i32 4, ptr %num2_0, align 4
%addInts_1 = alloca i32, align 4
%result_1 = alloca i32, align 4
store i32 5, ptr %result_1, align 4
store i32 5, ptr %addInts_1, align 4
%2 = call i32 (ptr, ...) @printf(ptr noundef @.str1)

  ret i32 1 
}
 declare i32 @printf(ptr noundef, ...) #1
attributes #1 = { "frame-pointer"="all" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "tune-cpu"="generic" }
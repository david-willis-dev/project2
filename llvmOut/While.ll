@.str3 = private unnamed_addr constant [4 x i8] c"55\0A\00", align 1
@.str2 = private unnamed_addr constant [8 x i8] c"Sum is \00", align 1
@.str1 = private unnamed_addr constant [34 x i8] c"Enter an integer greater than 0: \00", align 1
define dso_local noundef i32 @main() #1 {
%num_0 = alloca i32, align 4
%sum_0 = alloca i32, align 4
store i32 0, ptr %sum_0, align 4
%2 = call i32 (ptr, ...) @printf(ptr noundef @.str1)
store i32 10, ptr %num_0, align 4
store i32 20, ptr %sum_0, align 4
store i32 8, ptr %num_0, align 4
store i32 9, ptr %num_0, align 4
store i32 28, ptr %sum_0, align 4
store i32 7, ptr %num_0, align 4
store i32 8, ptr %num_0, align 4
store i32 35, ptr %sum_0, align 4
store i32 6, ptr %num_0, align 4
store i32 7, ptr %num_0, align 4
store i32 41, ptr %sum_0, align 4
store i32 5, ptr %num_0, align 4
store i32 6, ptr %num_0, align 4
store i32 46, ptr %sum_0, align 4
store i32 4, ptr %num_0, align 4
store i32 5, ptr %num_0, align 4
store i32 50, ptr %sum_0, align 4
store i32 3, ptr %num_0, align 4
store i32 4, ptr %num_0, align 4
store i32 53, ptr %sum_0, align 4
store i32 2, ptr %num_0, align 4
store i32 3, ptr %num_0, align 4
store i32 55, ptr %sum_0, align 4
store i32 1, ptr %num_0, align 4
store i32 2, ptr %num_0, align 4
store i32 56, ptr %sum_0, align 4
store i32 0, ptr %num_0, align 4
store i32 1, ptr %num_0, align 4
store i32 56, ptr %sum_0, align 4
store i32 -1, ptr %num_0, align 4
%3 = call i32 (ptr, ...) @printf(ptr noundef @.str2)
%4 = call i32 (ptr, ...) @printf(ptr noundef @.str3)

  ret i32 1 
}
 declare i32 @printf(ptr noundef, ...) #1
attributes #1 = { "frame-pointer"="all" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "tune-cpu"="generic" }
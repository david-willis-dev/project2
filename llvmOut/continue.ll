@.str7 = private unnamed_addr constant [11 x i8] c"Left Loop\0A\00", align 1
@.str6 = private unnamed_addr constant [12 x i8] c"Iteration \0A\00", align 1
@.str5 = private unnamed_addr constant [12 x i8] c"Iteration \0A\00", align 1
@.str4 = private unnamed_addr constant [12 x i8] c"Iteration \0A\00", align 1
@.str3 = private unnamed_addr constant [12 x i8] c"Iteration \0A\00", align 1
@.str2 = private unnamed_addr constant [12 x i8] c"Iteration \0A\00", align 1
@.str1 = private unnamed_addr constant [15 x i8] c"Entering Loop\0A\00", align 1
define dso_local noundef i32 @main() #1 {
%i_0 = alloca i32, align 4
%2 = call i32 (ptr, ...) @printf(ptr noundef @.str1)
store i32 1, ptr %i_0, align 4
%3 = call i32 (ptr, ...) @printf(ptr noundef @.str2)
store i32 2, ptr %i_0, align 4
%4 = call i32 (ptr, ...) @printf(ptr noundef @.str3)
store i32 3, ptr %i_0, align 4
%5 = call i32 (ptr, ...) @printf(ptr noundef @.str4)
store i32 4, ptr %i_0, align 4
%6 = call i32 (ptr, ...) @printf(ptr noundef @.str5)
store i32 5, ptr %i_0, align 4
%7 = call i32 (ptr, ...) @printf(ptr noundef @.str6)
%8 = call i32 (ptr, ...) @printf(ptr noundef @.str7)

  ret i32 1 
}
 declare i32 @printf(ptr noundef, ...) #1
attributes #1 = { "frame-pointer"="all" "no-trapping-math"="true" "stack-protector-buffer-size"="8" "tune-cpu"="generic" }
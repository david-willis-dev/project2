program EchoInteger;
var
    num: integer;
    sum: integer = 0;
begin
    write('Enter an integer greater than 0: ');
    readln(num);
    while num>0 do
    begin
       sum := sum + num;
       num := num - 1;
    end;
    write('Sum is : ');
    writeln(sum);
end.

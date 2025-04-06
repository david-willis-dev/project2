program EchoInteger;
var
    num, sum: integer;
begin
    sum := 0;
    write('Enter an integer greater than 0: ');
    readln(num);
    while num>0 do
    begin
       sum := sum + num;
       num := num - 1;
    end;
    write('Sum is ');
    writeln(sum);
end.

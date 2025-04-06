program functionTest;

function double(num1: integer): integer;

var
   result: integer;

begin

   result := num1 + num1;
   max := result;
end;

var
  num1, num2, result: integer;
begin
  num1 := 3;
  num2 := 5;
  result := double(num1);

  writeln(num1, ' doubled is', result);
end.
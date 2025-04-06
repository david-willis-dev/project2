program functionTest;

function addInts(num1, num2: integer): integer;

var
   result: integer;

begin

   result := num1 + num2;
   addInts := result;
end;

var
  num1, num2, result: integer;
begin
  num1 := 1;
  num2 := 4;
  result := addInts(num1, num2);

  writeln(num1, ' plus ', num2, ' is ', result);
end.
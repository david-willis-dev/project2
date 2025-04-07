program main;
    type ExampleClass = class
        public
            key: integer;
       end;

    var x: ExampleClass;

begin
  x := ExampleClass;
  write('Enter a number to save as the class variable: ');
  readln(x.key);
  writeln(x.key);
end.
program main;
    type ExampleClass = class
        private
            key: integer;
       end;

    var x: ExampleClass;

begin
  x := ExampleClass;
  writeln('Trying to access private variable... ');
  x.key := 90;
end.
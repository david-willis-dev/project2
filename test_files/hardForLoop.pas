program SimpleForLoop;
var
  i: integer;
begin

  writeln('Entering Loop');

  for i := 1 to 5 do
  begin
    writeln('Iteration ');
    continue;
    write(i);
  end;
  writeln('Left Loop');

end.
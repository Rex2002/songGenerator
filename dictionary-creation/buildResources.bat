@echo off
set "py-cmd=py"

echo Building Resources...
call %py-cmd% forbiddenNouns.py
call %py-cmd% generateDict.py
echo Done
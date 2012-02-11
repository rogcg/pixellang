.486
.model flat, stdcall
option casemap nodene
include \masm32\include\kernel32.inc
include \masm32\include\masm32.inc
include \masm32\lib\kernel32.lib
include lib\masm32\include\masm32.lib
.data
	 a1 DD 0
	 a2 DD 0
	 r2 DD 0.0r
	 r4 DD 0.0r
	 r3 DD 0.0r
	 s1 DB 256 dup(0)
	 s2 DB 256 dup(0)
	 name DB 256 dup(0)
	 temp0 DB "Test"
.code
start:
	 mov eax,20
	 mov a1, eax
	 mov eax,r3
	 add eax,45.5
	 mov r2, eax
	 mov eax,r4
	 mov ebx,12.2
	 cmp eax,ebx
	 jle label0
	 mov eax,2
	 mov a1, eax
	 mov eax,34.2
	 mov r3, eax
	 jmp label1
label0:
	 mov eax,3
	 mov a1, eax
label1:
label2:
	 mov eax,23.2
	 mov ebx,r4
	 cmp eax,ebx
	 jge label3
	 mov eax,2
	 mov a1, eax
	 mov eax,34.8
	 mov r3, eax
	 jmp label2
label3:
	 push 256
	 push offset a2
	 call StdIn
	 push offset "Test"
	 call StdOut
	 push 0
	 call exit process
end start

/* Multiboot header */
.set ALIGN,    1<<0
.set MEMINFO,  1<<1
.set FLAGS,    ALIGN | MEMINFO
.set MAGIC,    0x1BADB002
.set CHECKSUM, -(MAGIC + FLAGS)

.section .multiboot
.align 4
.long MAGIC
.long FLAGS
.long CHECKSUM

/* Stack */
.section .bss
.align 16
stack_bottom:
.skip 16384 # 16 KiB
stack_top:

/* Entry point */
.section .text
.global _start
.type _start, @function
_start:

	/* Stack pointer */
	mov $stack_top, %esp

	/* Initialization code here */


	/* Jump to c/c++ code */
	call kernel_main

	/* Halt the processor */
	cli
1:	hlt
	jmp 1b

/* _start size (for debugging/call tracing) */
.size _start, . - _start

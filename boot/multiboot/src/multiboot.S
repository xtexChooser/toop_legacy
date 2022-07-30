#define ASM_FILE
#define __ELF__ 1
#include "multiboot.h"

#ifdef __ELF__
    #define AOUT_KLUDGE 0
#else
    #define MULTIBOOT_AOUT_KLUDGE 0x10000
    #define AOUT_KLUDGE MULTIBOOT_AOUT_KLUDGE
#endif

#define MULTIBOOT_HEADER_FLAGS MULTIBOOT_PAGE_ALIGN | AOUT_KLUDGE

.text
.globl  start, _start

start:
_start:
    jmp multiboot_entry

.align  4
multiboot_header:
    .long   MULTIBOOT_HEADER_MAGIC
    .long   MULTIBOOT_HEADER_FLAGS
    .long   -(MULTIBOOT_HEADER_MAGIC + MULTIBOOT_HEADER_FLAGS)
#ifndef __ELF__
    .long   multiboot_header
    .long   _start
    .long   _edata
    .long   _end
    .long   multiboot_entry
#else /* ! ELF */
    .long   0
    .long   0
    .long   0
    .long   0
    .long   0
#endif /* ELF */
    .long   0
    .long   0
    .long   0
    .long   0

multiboot_entry:
    // Configure stack
    movl    $0x7c00, %esp

    // Reset EFLAGS
    pushl   $0
    popf

    pushl   %ebx
    pushl   %eax
    call    multiboot_entry_c

loop:
    hlt
    jmp     loop

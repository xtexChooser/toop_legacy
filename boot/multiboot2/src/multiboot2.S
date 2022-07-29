#define ASM_FILE 1
#define __ELF__ 1
#include "multiboot2.h"

#ifdef __ELF__
    #define AOUT_KLUDGE 0
#else
    #define MULTIBOOT_AOUT_KLUDGE 0x10000
    #define AOUT_KLUDGE MULTIBOOT_AOUT_KLUDGE
#endif

.text
.globl  start, _start

start:
_start:
    jmp multiboot_entry

.align  8
multiboot_header:
    .long   MULTIBOOT2_HEADER_MAGIC
    .long   MULTIBOOT_ARCHITECTURE_I386
    .long   multiboot_header_end - multiboot_header
    .long   -(MULTIBOOT2_HEADER_MAGIC + MULTIBOOT_ARCHITECTURE_I386 + (multiboot_header_end - multiboot_header))

#ifndef __ELF__
    address_tag:
        .short  MULTIBOOT_HEADER_TAG_ADDRESS
        .short  MULTIBOOT_HEADER_TAG_OPTIONAL
        .long   address_tag_end - address_tag
        .long   multiboot_header
        .long   start
        .long   _edata
        .long   _end
    address_tag_end:
    entry_address_tag:
        .short  MULTIBOOT_HEADER_TAG_ENTRY_ADDRESS
        .short  MULTIBOOT_HEADER_TAG_OPTIONAL
        .long   entry_address_tag_end - entry_address_tag
        .long   multiboot_entry
    entry_address_tag_end:
#endif

.short  MULTIBOOT_HEADER_TAG_END
.short  0
.long   8

multiboot_header_end:

multiboot_entry:
    // Configure stack
    movl    $end_stack, %esp

    // Reset EFLAGS
    pushl   $0
    popf

    pushl   %ebx
    pushl   %eax
    call    multiboot_entry_c

loop:
    hlt
    jmp     loop

.comm stack, 0x1000
end_stack:

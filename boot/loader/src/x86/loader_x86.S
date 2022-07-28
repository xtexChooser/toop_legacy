.macro init_gdt
.align 8
    gdt:
        .quad 0x0000000000000000
        .quad 0x00CF9A000000FFFF // code seg
        .quad 0x00CF92000000FFFF // data seg
    gdt_end:
.endm

.macro init_gdtr
.align 8
gdtr:
    .word gdt_end - gdt // limit
    .long gdt // base
.endm

#define load_init_gdtr lgdt gdtr
#define DATA_SEG_SELECTOR $0b10000
#define CODE_SEG_SELECTOR $0b01000

.macro switch_data_segs
    mov     DATA_SEG_SELECTOR, %ax
    mov     %ax, %ds
    mov     %ax, %es
    mov     %ax, %ss
.endm

.macro enable_cr0_pe
    mov     %cr0, %eax
    or      $1, %eax
    mov     %eax, %cr0
.endm

.macro fast_enable_a20
    inb     $0x92, %al
    or      $2, %al
    outb    %al, $0x92
.endm

.macro disable_non_maskable_int
    inb     $0x70, %al
    or      $0b10000000, %al
    outb    %al, $0x70
    inb     $0x71, %al
.endm

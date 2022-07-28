.section .text.eltorito, "ax", @progbits
.global start, _start
.code16

#include "../../loader/src/x86/loader_x86.S"

.org 0
start:
_start:
    jmpl    $0, $eltorito_entrypoint

.org 8

bi_PrimaryVolumeDescriptor: .long  0
bi_BootFileLocation:        .long  0
bi_BootFileLength:          .long  0
bi_Checksum:                .long  0
bi_Reserved:                .space 40

eltorito_entrypoint:
    xor     %ax, %ax
    mov     %ax, %ds
    mov     %ax, %es
    mov     %ax, %ss
    mov     %ax, %sp

    // Load the whole ElTorito loader
    mov     %dl, boot_bios_drive_number

    // Initialize LBA
    movl    bi_BootFileLocation, %eax
    incl    %eax
    // Initailize buffer offset
    movw    $0x8400, edd_disk_addr_packet_buffer_offset
    // Initialize target sectors count
    mov     bi_BootFileLength, %ebx
    shrl    $11, %ebx // ebx /= 2048

    // Read with EDD
    .edd_read:
        // Update sectors count
        mov     %eax, edd_disk_addr_packet_target_LBA
        mov     %ebx, %ecx
        cmp     $0x7f, %ecx
        jle     .edd_read_call
        mov     $0x7f, %ecx
    .edd_read_call:
        // Call read
        movb    %cl, edd_disk_addr_packet_sectors_count
        pusha
        mov     $0x42, %ah
        mov     $edd_disk_addr_packet, %si
        mov     boot_bios_drive_number, %dl
        int     $0x13
        popa
        jc      .edd_read
        // Initialize next read
        subl    %ecx, %ebx
        addl    %ecx, %eax
        cmp     $0, %ebx
        jnz     .edd_read

.code16gcc
    cli
    disable_non_maskable_int
    fast_enable_a20
    load_init_gdtr
    enable_cr0_pe
    switch_data_segs
    mov     $0x7c00, %esp
    jmpl    CODE_SEG_SELECTOR, $eltorito_entrypoint_c

.org 0x1fe
.word 0xaa55

edd_disk_addr_packet:
    .byte   (edd_disk_addr_packet_end - edd_disk_addr_packet) // size
    .byte   0 // reversed
edd_disk_addr_packet_sectors_count:
    .byte   0 // sectors count
    .byte   0 // reversed
edd_disk_addr_packet_buffer_offset:
    .word   0 // buffer offset
    .word   0 // buffer segment
edd_disk_addr_packet_target_LBA:
    .long   0 // target LBA low 32-bits
    .long   0 // target LBA high 32-bits
edd_disk_addr_packet_end:

boot_bios_drive_number: .byte 0

init_gdtr
init_gdt

.org 0x800

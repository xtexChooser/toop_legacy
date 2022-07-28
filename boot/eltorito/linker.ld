ENTRY(eltorito_entrypoint_c)

SECTIONS {
	# . = 0x0007c00;
	
	.text : { *(.text.eltorito) *(.text) }
	.rodata : { *(.rodata) }
	.data : { *(.data) }
	.bss : { *(.bss COMMON) }
}

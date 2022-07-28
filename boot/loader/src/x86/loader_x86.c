unsigned long long gdt[] __attribute__((aligned(8))) = {
    __extension__ 0x0000000000000000ull,
    __extension__ 0x00CF9A000000FFFFull, // Code
    __extension__ 0x00CF92000000FFFFull  // Data
};

extern void *gdtr;

#define enter_protect_mode() \
  __asm__("cli"); \
  enable_a20(); \
  *(short *)gdtr = sizeof(gdt) - 1; \
  *(int *)(gdtr + 2) = (unsigned int)&gdt; \
  __asm__("lgdt %0" : "=m"(gdtr)); \
  __asm__("mov %cr0, %eax"); \
  __asm__("or $1, %eax"); \
  __asm__("mov %eax, %cr0"); \
  __asm__("sti"); \

#define enable_a20() \
  __asm__("inb $0x92, %al"); \
  __asm__("or $2, %al"); \
  __asm__("outb %al, $0x92"); \

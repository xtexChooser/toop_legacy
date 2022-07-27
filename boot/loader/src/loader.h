#pragma once
#include "libelf.h"

typedef void (*kernel_entry)();

void load_kernel(void *kernel, int kernel_size);

void *find_elf_load_base_addr(Elf32_Ehdr *elf);
void *find_elf_load_end_addr(Elf32_Ehdr *elf);

#pragma once
#include "libelf.h"

struct boot_reserved_mmap {
  unsigned long long base_addr;
  unsigned long long size;
};

struct boot_info {
  unsigned int kernel_base;
  unsigned int kernel_end;
  unsigned long long mem_lower;
  unsigned long long mem_upper;
  struct boot_reserved_mmap *mem_reserved_map;
  unsigned int mem_reserved_map_size;
};

typedef void (*kernel_entry)(struct boot_info *boot_info);

void load_kernel(struct boot_info *boot_info);

void *find_elf_load_base_addr(Elf32_Ehdr *elf);
void *find_elf_load_end_addr(Elf32_Ehdr *elf);

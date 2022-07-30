#pragma once
#include "libelf.h"

struct __attribute__((__packed__)) boot_reserved_mmap {
  unsigned long long base_addr;
  unsigned long long size;
};

struct __attribute__((__packed__)) boot_info {
  int kernel_base;
  int kernel_end;
  struct boot_reserved_mmap *mem_reserved_map;
  int mem_reserved_map_size;
};

typedef void (*kernel_entry)(struct boot_info *boot_info);

void load_kernel(struct boot_info *boot_info);

void *find_elf_load_base_addr(Elf32_Ehdr *elf);
void *find_elf_load_end_addr(Elf32_Ehdr *elf);

#include "loader.h"

void load_kernel(struct boot_info *boot_info) {
  void *kernel = (void *)boot_info->kernel_base;
  void *kernel_end = (void *)boot_info->kernel_end;
  int kernel_size = (int)(kernel_end - kernel);
  Elf32_Ehdr *kernel_elf = (Elf32_Ehdr *)kernel;
  kernel_entry entry_point = (kernel_entry)kernel_elf->e_entry;

  // Locate program header
  void *load_base_addr = find_elf_load_base_addr(kernel_elf);
  void *load_end = find_elf_load_end_addr(kernel_elf);

  // Move ELF
  if ((kernel >= load_base_addr && kernel <= load_end) ||
      (kernel_end >= load_base_addr && kernel_end <= load_end) ||
      (kernel <= load_base_addr && kernel_end >= load_end)) {
    int move_times = kernel_size / sizeof(int);
    int *move_dest = load_end;
    if (kernel_end > load_end)
      move_dest = kernel_end;
    move_dest++;
    kernel_elf = (Elf32_Ehdr *)move_dest;
    int *move_src = kernel;
    for (int i = 0; i < move_times; i++) {
      *move_dest = *move_src;
      move_dest++;
      move_src++;
    }
  }

  // Resolve LOAD program headers
  Elf32_Phdr *program_header =
      (Elf32_Phdr *)(((void *)kernel_elf) + kernel_elf->e_phoff);
  for (int i = 0; i < kernel_elf->e_phnum; i++) {
    if (program_header->p_type == PT_LOAD) {
      int move_times = program_header->p_filesz / sizeof(int);
      int *move_src = (int *)((void *)kernel_elf + program_header->p_offset);
      int *move_dest = (int *)program_header->p_paddr;
      for (int i = 0; i < move_times; i++) {
        *move_dest = *move_src;
        move_dest++;
        move_src++;
      }
    }
    program_header++;
  }

  // Call entry point
  boot_info->kernel_base = (int)kernel_elf;
  boot_info->kernel_end = (int)load_end;
  entry_point(boot_info);
}

void *find_elf_load_base_addr(Elf32_Ehdr *elf) {
  Elf32_Phdr *program_header = (Elf32_Phdr *)(((void *)elf) + elf->e_phoff);
  void *load_base_addr = 0;
  for (int i = 0; i < elf->e_phnum; i++) {
    if (program_header->p_type == PT_LOAD) {
      if ((void *)program_header->p_paddr < load_base_addr) {
        load_base_addr = (void *)program_header->p_paddr;
      }
    }
    program_header++;
  }
  return load_base_addr;
}

void *find_elf_load_end_addr(Elf32_Ehdr *elf) {
  Elf32_Phdr *program_header = (Elf32_Phdr *)(((void *)elf) + elf->e_phoff);
  void *load_end = 0;
  for (int i = 0; i < elf->e_phnum; i++) {
    if (program_header->p_type == PT_LOAD) {
      void *cur_load_end = (void *)(((int)program_header->p_paddr) +
                                    (int)program_header->p_memsz);
      if (cur_load_end > load_end) {
        load_end = cur_load_end;
      }
    }
    program_header++;
  }
  return load_end;
}

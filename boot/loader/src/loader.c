#include "loader.h"

void load_kernel(void *kernel, int kernel_size) {
  Elf32_Ehdr *kernel_elf = (Elf32_Ehdr *)kernel;
  kernel_entry entry_point = (kernel_entry)kernel_elf->e_entry;

  // Locate program header
  void *load_base_addr = find_elf_load_base_addr(kernel_elf);
  void *load_end = find_elf_load_end_addr(kernel_elf);

  // Move ELF
  void *kernel_end = kernel + kernel_size;
  if ((kernel >= load_base_addr && kernel <= load_end) ||
      (kernel_end >= load_base_addr && kernel_end <= load_end) ||
      (kernel <= load_base_addr && kernel_end >= load_end)) {
    int move_times = kernel_size / sizeof(int);
    int *move_dest = load_end;
    if (kernel_end > load_end)
      move_dest = kernel_end;
    kernel_elf = (Elf32_Ehdr *)move_dest;
    int *move_src = kernel;
    for (int i = 0; i < move_times; i++) {
      *move_dest = *move_src;
      move_dest += sizeof(int);
      move_src += sizeof(int);
    }
  }

  // Resolve program headers
  Elf32_Phdr *program_header = (Elf32_Phdr *)kernel_elf->e_phoff;
  for (int i = 0; i < kernel_elf->e_phnum; i++) {
    if (program_header->p_type == PT_LOAD) {
      int move_times = program_header->p_filesz / sizeof(int);
      int *move_src = (int *)kernel_elf + program_header->p_offset;
      int *move_dest = (int *)program_header->p_paddr;
      for (int i = 0; i < move_times; i++) {
        *move_dest = *move_src;
        move_dest += sizeof(int);
        move_src += sizeof(int);
      }
    }
    program_header += sizeof(Elf32_Phdr);
  }

  // Call entry point
  entry_point();
}

void *find_elf_load_base_addr(Elf32_Ehdr *elf) {
  Elf32_Phdr *program_header = (Elf32_Phdr *)elf->e_phoff;
  void *load_base_addr = 0;
  for (int i = 0; i < elf->e_phnum; i++) {
    if (program_header->p_type == PT_LOAD) {
      if ((void *)program_header->p_paddr < load_base_addr) {
        load_base_addr = (void *)program_header->p_paddr;
      }
    }
    program_header += sizeof(Elf32_Phdr);
  }
  return load_base_addr;
}

void *find_elf_load_end_addr(Elf32_Ehdr *elf) {
  Elf32_Phdr *program_header = (Elf32_Phdr *)elf->e_phoff;
  void *load_end = 0;
  for (int i = 0; i < elf->e_phnum; i++) {
    if (program_header->p_type == PT_LOAD) {
      Elf32_Addr cur_load_end =
          program_header->p_paddr + program_header->p_memsz;
      if ((void *)cur_load_end > load_end) {
        load_end = (void *)cur_load_end;
      }
    }
    program_header += sizeof(Elf32_Phdr);
  }
  return load_end;
}

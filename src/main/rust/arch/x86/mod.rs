extern crate x86;

//pub mod descriptors;
pub mod gdt;

pub const PAGE_SIZE_KB: usize = 4;

pub fn init(_boot_info: &crate::boot::BootInfo) {
}

pub fn pre_init_mem(_boot_info: &crate::boot::BootInfo) {
    //gdt::init();
}

pub fn init_mem(_boot_info: &crate::boot::BootInfo) {
}

#[derive(Debug, Clone, Copy)]
#[repr(C)]
pub struct BootInfo<'a> {
    pub kernel_base: u32,
    pub kernel_end: u32,
    pub mem_lower_kb: u64,
    pub mem_upper_kb: u64,
    pub mem_reserved_map: &'a [BootReversedMmap],
    pub mem_reserved_map_size: u32,
}

#[derive(Debug, Clone, Copy)]
#[repr(C)]
pub struct BootReversedMmap {
    pub base_addr: u64,
    pub size: u64,
}

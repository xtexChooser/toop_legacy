use crate::arch;

pub static mut TOTAL_PAGES: usize = 0;

pub const BUDDY_LAYER_COUNT: usize = 4;
static mut BUDDY_LAYERS_STUB: [usize; 0] = [];
pub static mut BUDDY_LAYERS: [*mut [usize]; BUDDY_LAYER_COUNT] =
    unsafe { [&mut BUDDY_LAYERS_STUB; BUDDY_LAYER_COUNT] };

pub fn init(boot_info: &crate::boot::BootInfo) {
    crate::arch::pre_init_mem(boot_info);
    /*let mut last_layer_size: usize =
        (boot_info.mem_upper_kb - boot_info.mem_lower_kb) as usize / arch::PAGE_SIZE_KB;
    last_layer_size = last_layer_size ^ 1;
    unsafe {
        TOTAL_PAGES = last_layer_size;
    }
    let mut layer_pointer: &'static mut [usize] =
        unsafe { &mut *(boot_info.kernel_end as *mut [usize; 0]) };
    for layer in 0..BUDDY_LAYER_COUNT {
        let current_size = last_layer_size / 2 as usize;
        unsafe {
            BUDDY_LAYERS[layer] = layer_pointer;
        }
        unsafe {
            layer_pointer =
                &mut *((layer_pointer.as_mut_ptr() as usize + current_size) as *mut [usize; 0]);
        }
        last_layer_size = current_size;
    }*/
    crate::arch::init_mem(boot_info);
}

pub fn alloc_pages(layer: u8) -> usize {
    //
    return 0 as usize;
}

use crate::arch::x86::dtables::*;
use crate::arch::x86::segmentation::*;
use crate::arch::x86::Ring;

pub const GDT_SIZE: usize = 256;
pub static mut GDT: [Descriptor; GDT_SIZE] = [Descriptor::NULL; GDT_SIZE];
pub static mut GDT_POINTER: DescriptorTablePointer<Descriptor> = DescriptorTablePointer {
    limit: 0,
    base: core::ptr::null(),
};
pub static mut NEXT_GDT_ENTRY: usize = 1;
pub static CODE_SEGMENT_SELECTOR: SegmentSelector = SegmentSelector::new(1, Ring::Ring0);
pub static DATA_SEGMENT_SELECTOR: SegmentSelector = SegmentSelector::new(2, Ring::Ring0);
pub static PROTECTED_CODE_SEGMENT_SELECTOR: SegmentSelector = SegmentSelector::new(3, Ring::Ring3);

#[rustfmt::skip]
pub fn init() {
    unsafe {
        // @TODO: dynamic alloc GDT
        // Code
        GDT[1] =
            DescriptorBuilder::code_descriptor(0x00000000, 0xffffffff, CodeSegmentType::ExecuteRead)
                .present()
                .limit_granularity_4kb()
                .dpl(Ring::Ring0)
                .db()
                .finish();
        // Data
        GDT[2] =
            DescriptorBuilder::data_descriptor(0x00000000, 0xffffffff, DataSegmentType::ReadWrite)
                .present()
                .limit_granularity_4kb()
                .dpl(Ring::Ring0)
                .db()
                .finish();
        // Protected Code
        GDT[3] =
            DescriptorBuilder::code_descriptor(0x00000000, 0xffffffff, CodeSegmentType::Execute)
                .present()
                .limit_granularity_4kb()
                .dpl(Ring::Ring3)
                .db()
                .finish();
        GDT_POINTER = DescriptorTablePointer::new_from_slice(&GDT);
        lgdt(&GDT_POINTER);
        load_cs(CODE_SEGMENT_SELECTOR);
        load_ds(DATA_SEGMENT_SELECTOR);
        load_ss(DATA_SEGMENT_SELECTOR);
    }
}

#pragma version(1)

rs_allocation gIn;
rs_allocation gOut;
rs_script gScript;

static int mImageWidth;
static int mImageNeedWidth;
const uchar4 *gPixels;
const uchar4 *gPixels2;

void init() {
}

static const int kBlurWidth = 20;
static const float kMultiplier = 1.0f / (float)(kBlurWidth * 2 + 1);

void root(const uchar4 *v_in, uchar4 *v_out, const void *usrData, uint32_t x, uint32_t y) {
    float4 original = rsUnpackColor8888(*v_in);
	
	
    *v_out = rsPackColorTo8888(colour);
}


void filter() {
    mImageWidth = rsAllocationGetDimX(gIn);
    rsDebug("Image size is ", rsAllocationGetDimX(gIn), rsAllocationGetDimY(gOut));
    rsForEach(gScript, gIn, gOut, NULL);
}
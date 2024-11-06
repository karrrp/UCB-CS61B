class Main {
    public int[] twoSum(int[] nums, int target) {
        int[] targets = new int[2];
        for (int i = 0; i < nums.length; i++) {
            int index = findIndex(nums, target - nums[i]);
            if (index != nums.length && index != i ) {
                targets[0] = index;
                targets[1] = i;
                return targets;
            }
        }
        return null;
    }
    private int findIndex(int[] nums, int reminder) {
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == reminder)
                return i;
        }
        return nums.length;
    }
}
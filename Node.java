package com.example.android.opengl;

import android.util.Log;

public class Node{
	public static float floatMAX ;		//maximum float range
	public static float offsetX;			//Reference coordinates
	public static float offsetY;
	public static float H;							//Height of original rectangle
	public static float W;							//Width of original rectangle
	public static boolean vert,prevVert;					//vert value of previous operation 
	public static int prevIndex;					//Index value of previous operation..initialised to 0
	
	public static int count,index,end;
	public static float w,h;
	public static class TreeNode {
		TreeNode Parent;
		TreeNode child[];
		int count;
		float area;
		float width, tmp_width;						//tmp_width is used for back-tracking to previous value of width
		float height, tmp_height;
		float aspectLast;							//Aspect ratio 
		float X;									//Coordinates of the rectangle of given width and height
		float Y;
		
		public TreeNode() {							//Constructor
			count = 0;
			area = 0;
			width = 0;
			tmp_width = 0;
			height = 0;
			tmp_height = 0;
			aspectLast = 0;
			X = 0;
			Y = 0;
			
		}
	}

	public static void preorder(TreeNode v) {		//To traverse the tree in preorder way of traversal
		if(v.child!=null){
			w = v.width;
			h = v.height;
			while(end != v.count){
				squarify(v, v.child);			//squarify function finds the squarified treemap of this node
				//System.out.println("End: "+end);
			}
			for (int i = 0; i < v.count; i++) {
				preorder(v.child[i]);
			}
		}
		//else Leaf node!
	}

	private static float getAspectRatio(float h, float w) {	//gets the aspect ratio of a node
		return Math.max(h / w, w / h);
	}

	private static boolean DrawVertically(float w, float h) { //checks if nodes are to be drawn vertically or horizontally (if return value is true then, vertically)
		return w > h;
	}

	private static boolean compareAspect(TreeNode[] child, int end,float aspectCurr) { //find the aspect ratio which is more closer to 1 than the other
		return (Math.abs(aspectCurr - 1) > Math.abs(child[end].aspectLast - 1));
	}

	private static void squarify(TreeNode v, TreeNode[] child) {
		count++;
		Log.i("smruti", "count="+count);
		//System.out.println("Count: "+count);
		vert = DrawVertically(w, h);								//initialising vert
		end = index;
		float sum;												
		float aspectCurr=floatMAX;											//setting aspectCurr to maximum value possible helpful for back-tracking
		do {
			if(index!=v.count-1){											//as last item has to placed in the remaining area
				float totalArea = 0;
				for (int t = index; t <= end; t++) {
					totalArea+=child[t].area;
				}
				//System.out.println("Total area:"+totalArea);
				for (int i = index; i <= end; i++) {
					if (vert) {
						child[i].width = totalArea / h;
						child[i].height = child[i].area / child[i].width;
					} else {
						child[i].height = totalArea / w;
						child[i].width = child[i].area / child[i].height;
					}
				} 
				child[end].aspectLast = getAspectRatio(child[end].height, child[end].width); //finding aspect ratio of last item
			}
			if (compareAspect(child, end,aspectCurr)&&(index!=v.count-1)&&(end!=v.count-1)) {					//here again last item has no need to be compared
				//Aspect ratio is closer to 1 ! Adding next item.....
				aspectCurr = child[end].aspectLast;
				for (int i = index; i <= end; i++) {
					child[i].tmp_height = child[i].height;
					child[i].tmp_width = child[i].width;
				}
				end++; // add next item
				continue;
				
			}else {
				//remove that item
				if(index==v.count-1) 													//incrementing end value for last item
					end++;
				if(end==v.count-1){
					if(compareAspect(child,end,aspectCurr)){
						
						end++;
					}
				}
				for (int j = index; j < end; j++) {
					if(index==v.count-1) {												//for last item
						child[j].height = h;
						child[j].width = w;
					}
					else{
						if(end!=v.count){
							child[j].height = child[j].tmp_height;
							child[j].width = child[j].tmp_width;
						}
					}
					child[j].aspectLast= getAspectRatio(child[j].height, child[j].width);
					if(j==0){															//find coordinates of first item
						child[j].X=offsetX;
						child[j].Y=offsetY;
					}
					else if(j==index){							
						if(prevVert){
							child[index].X=child[prevIndex].X+child[prevIndex].width;
							child[index].Y=child[prevIndex].Y;
						}
						else{
							child[index].Y=child[prevIndex].Y+child[prevIndex].height;
							child[index].X=child[prevIndex].X;
						}
						prevVert=vert;
						prevIndex=index;
					}
					else{
						sum=0;
						if(vert){
							for(int k=index;k<j;k++){
								sum+=child[k].height;
							}
							child[j].X=child[index].X;
							child[j].Y=child[index].Y+sum;
						}
						else{
							for(int k=index;k<j;k++){
								sum+=child[k].width;
							}
							child[j].Y=child[index].Y;
							child[j].X=child[index].X+sum;
						}
					}
																				
					Log.i("TAG","X coordinate:"+child[j].X);				//Display
					Log.i("TAG","Y coordinate:"+child[j].Y);
					Log.i("TAG","height:"+child[j].height);
					Log.i("TAG","width: "+child[j].width);
					
					
				}
				if (end != v.count) {
					if (vert) {
						w=w - child[index].width;
					}
					else{
						h=h - child[index].height;
					}
					index=end;
					return;
				} 
				break;
			}
		} while (end != v.count);

	}

	public static TreeNode mainFunction(TreeNode root) {
		floatMAX=999999;
		offsetX=root.X;
		offsetY=root.Y;
		prevIndex=0;
		count=0;
		index=0;
		end=0;
		
		root.Parent = null;
		H=root.height;
		W=root.width;
		prevVert= DrawVertically(W, H);
		
		int number=root.count;
		
		TreeNode a[]=root.child;
		float totalSum=0;
		for(int j=0;j<root.count;j++)
			totalSum+=a[j].area;
		float ratio=root.area/totalSum;
		for (int i = 0; i < number; i++) {
			a[i].area=a[i].area*ratio;
			a[i].Parent = root;
			a[i].child = null;
			a[i].count = 0;
			root.child[i] = a[i];
			Log.i("TAG","area:"+root.child[i].area);
		}

		if (root.child != null) {
			preorder(root);
		}
		return root;

	}

}

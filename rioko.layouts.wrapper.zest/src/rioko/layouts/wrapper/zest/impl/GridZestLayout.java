package rioko.layouts.wrapper.zest.impl;

import org.eclipse.zest.layouts.LayoutAlgorithm;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.GridLayoutAlgorithm;

import rioko.layouts.wrapper.zest.ZestLayoutWrapper;

public class GridZestLayout extends ZestLayoutWrapper {

	@Override
	protected LayoutAlgorithm getAlgorithm() {
		return new GridLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
	}

}

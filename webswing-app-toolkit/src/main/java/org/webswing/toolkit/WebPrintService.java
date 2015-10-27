package org.webswing.toolkit;

import java.util.Locale;

import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.ServiceUIFactory;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.AttributeSetUtilities;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.ColorSupported;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.CopiesSupported;
import javax.print.attribute.standard.Fidelity;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.RequestingUserName;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.Sides;
import javax.print.event.PrintServiceAttributeListener;

public class WebPrintService implements PrintService {

	static final DocFlavor[] supportedDocFlavors = { DocFlavor.SERVICE_FORMATTED.PAGEABLE, DocFlavor.SERVICE_FORMATTED.PRINTABLE };
	private static final Class<?>[] suppAttrCats = { Chromaticity.class, Copies.class, Fidelity.class, JobName.class, Media.class, MediaPrintableArea.class, OrientationRequested.class, PageRanges.class, RequestingUserName.class, SheetCollate.class, Sides.class };
	private static final MediaSizeName[] mediaSizes = { MediaSizeName.NA_LETTER, MediaSizeName.TABLOID, MediaSizeName.LEDGER, MediaSizeName.NA_LEGAL, MediaSizeName.EXECUTIVE, MediaSizeName.ISO_A3, MediaSizeName.ISO_A4, MediaSizeName.ISO_A5, MediaSizeName.ISO_B4, MediaSizeName.ISO_B5 };

	private static WebPrintService thisService = new WebPrintService();

	public static WebPrintService getService() {
		return thisService;
	}

	@Override
	public String getName() {
		return "WebPrintService";
	}

	@Override
	public DocPrintJob createPrintJob() {
		return new WebPrintJob(this);
	}

	@Override
	public void addPrintServiceAttributeListener(PrintServiceAttributeListener listener) {
	}

	@Override
	public void removePrintServiceAttributeListener(PrintServiceAttributeListener listener) {
	}

	@Override
	public PrintServiceAttributeSet getAttributes() {
		HashPrintServiceAttributeSet set = new HashPrintServiceAttributeSet();
		set.add(ColorSupported.SUPPORTED);

		return AttributeSetUtilities.unmodifiableView(set);
	}

	@SuppressWarnings("unchecked")
	public <T extends PrintServiceAttribute> T getAttribute(Class<T> category) {
		if (category == ColorSupported.class) {
			return (T) ColorSupported.SUPPORTED;
		}
		return null;
	}

	@Override
	public DocFlavor[] getSupportedDocFlavors() {
		DocFlavor[] arrayOfDocFlavor = new DocFlavor[supportedDocFlavors.length];
		System.arraycopy(supportedDocFlavors, 0, arrayOfDocFlavor, 0, arrayOfDocFlavor.length);
		return arrayOfDocFlavor;
	}

	@Override
	public boolean isDocFlavorSupported(DocFlavor flavor) {
		DocFlavor[] arrayOfDocFlavor = getSupportedDocFlavors();
		for (int i = 0; i < arrayOfDocFlavor.length; i++) {
			if (flavor.equals(arrayOfDocFlavor[i])) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Class<?>[] getSupportedAttributeCategories() {
		Class<?>[] arrayOfClass = new Class[suppAttrCats.length];
		System.arraycopy(suppAttrCats, 0, arrayOfClass, 0, arrayOfClass.length);
		return arrayOfClass;
	}

	@Override
	public boolean isAttributeCategorySupported(Class<? extends Attribute> category) {
		for (int i = 0; i < suppAttrCats.length; i++) {
			if (category == suppAttrCats[i]) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object getDefaultAttributeValue(Class<? extends Attribute> category) {
		if (!isAttributeCategorySupported(category)) {
			return null;
		}
		if (category == Copies.class)
			return new Copies(1);
		if (category == Chromaticity.class)
			return Chromaticity.COLOR;
		if (category == Fidelity.class)
			return Fidelity.FIDELITY_FALSE;
		String str;
		if (category == Media.class) {
			str = Locale.getDefault().getCountry();
			if ((str != null) && ((str.equals("")) || (str.equals(Locale.US.getCountry())) || (str.equals(Locale.CANADA.getCountry())))) {
				return MediaSizeName.NA_LETTER;
			}
			return MediaSizeName.ISO_A4;
		}
		if (category == MediaPrintableArea.class) {
			str = Locale.getDefault().getCountry();
			float f3 = 0.5F;
			float f1;
			float f2;
			if ((str != null) && ((str.equals("")) || (str.equals(Locale.US.getCountry())) || (str.equals(Locale.CANADA.getCountry())))) {
				f1 = MediaSize.NA.LETTER.getX(25400) - 2.0F * f3;
				f2 = MediaSize.NA.LETTER.getY(25400) - 2.0F * f3;
			} else {
				f1 = MediaSize.ISO.A4.getX(25400) - 2.0F * f3;
				f2 = MediaSize.ISO.A4.getY(25400) - 2.0F * f3;
			}
			return new MediaPrintableArea(f3, f3, f1, f2, 25400);
		}
		if (category == OrientationRequested.class)
			return OrientationRequested.PORTRAIT;
		if (category == PageRanges.class)
			return new PageRanges(1, Integer.MAX_VALUE);
		if (category == SheetCollate.class)
			return SheetCollate.UNCOLLATED;
		if (category == Sides.class) {
			return Sides.ONE_SIDED;
		}

		return null;
	}

	@Override
	public Object getSupportedAttributeValues(Class<? extends Attribute> paramClass, DocFlavor paramDocFlavor, AttributeSet paramAttributeSet) {
		if (!isAttributeCategorySupported(paramClass)) {
			return null;
		}
		Object[] supportedValues;
		if (paramClass == Chromaticity.class) {
			supportedValues = new Chromaticity[1];
			supportedValues[0] = Chromaticity.COLOR;

			return supportedValues;
		}
		if (paramClass == JobName.class)
			return new JobName("", null);
		if (paramClass == RequestingUserName.class)
			return new RequestingUserName("", null);
		if (paramClass == OrientationRequested.class) {
			if ((paramDocFlavor == null) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE))) {

				supportedValues = new OrientationRequested[3];
				supportedValues[0] = OrientationRequested.PORTRAIT;
				supportedValues[1] = OrientationRequested.LANDSCAPE;
				supportedValues[2] = OrientationRequested.REVERSE_LANDSCAPE;
				return supportedValues;
			}
			return null;
		}
		if ((paramClass == Copies.class) || (paramClass == CopiesSupported.class)) {
			return new CopiesSupported(1, 1);
		}
		if (paramClass == Media.class) {
			supportedValues = new Media[mediaSizes.length];
			System.arraycopy(mediaSizes, 0, supportedValues, 0, mediaSizes.length);
			return supportedValues;
		}
		if (paramClass == Fidelity.class) {
			supportedValues = new Fidelity[2];
			supportedValues[0] = Fidelity.FIDELITY_FALSE;
			supportedValues[1] = Fidelity.FIDELITY_TRUE;
			return supportedValues;
		}
		if (paramClass == MediaPrintableArea.class) {
			if (paramAttributeSet == null) {
				return null;
			}
			MediaSize supportedMediaSizeValues = (MediaSize) paramAttributeSet.get(MediaSize.class);
			if (supportedMediaSizeValues == null) {
				Media localObject2 = (Media) paramAttributeSet.get(Media.class);
				if ((localObject2 != null) && ((localObject2 instanceof MediaSizeName))) {
					MediaSizeName localMediaSizeName = (MediaSizeName) localObject2;
					supportedMediaSizeValues = MediaSize.getMediaSizeForName(localMediaSizeName);
				}
			}
			if (supportedMediaSizeValues == null) {
				return null;
			}
			Object[] localObject2 = new MediaPrintableArea[1];
			float f1 = ((MediaSize) supportedMediaSizeValues).getX(25400);
			float f2 = ((MediaSize) supportedMediaSizeValues).getY(25400);

			float f3 = 0.5F;
			float f4 = 0.5F;
			if (f1 < 5.0F) {
				f3 = f1 / 10.0F;
			}
			if (f2 < 5.0F) {
				f4 = f2 / 10.0F;
			}
			localObject2[0] = new MediaPrintableArea(f3, f4, f1 - 2.0F * f3, f2 - 2.0F * f4, 25400);

			return localObject2;
		}
		if (paramClass == PageRanges.class) {
			if ((paramDocFlavor == null) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE))) {

				supportedValues = new PageRanges[1];
				supportedValues[0] = new PageRanges(1, Integer.MAX_VALUE);
				return supportedValues;
			}
			return null;
		}
		if (paramClass == SheetCollate.class) {
			if ((paramDocFlavor == null) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE))) {

				supportedValues = new SheetCollate[2];
				supportedValues[0] = SheetCollate.UNCOLLATED;
				supportedValues[1] = SheetCollate.COLLATED;
				return supportedValues;
			}
			supportedValues = new SheetCollate[1];
			supportedValues[0] = SheetCollate.UNCOLLATED;
			return supportedValues;
		}
		if (paramClass == Sides.class) {
			if ((paramDocFlavor == null) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PAGEABLE)) || (paramDocFlavor.equals(DocFlavor.SERVICE_FORMATTED.PRINTABLE))) {

				supportedValues = new Sides[3];
				supportedValues[0] = Sides.ONE_SIDED;
				supportedValues[1] = Sides.TWO_SIDED_LONG_EDGE;
				supportedValues[2] = Sides.TWO_SIDED_SHORT_EDGE;
				return supportedValues;
			}
			return null;
		}

		return null;
	}

	@Override
	public boolean isAttributeValueSupported(Attribute paramAttribute, DocFlavor paramDocFlavor, AttributeSet set) {
		Class<? extends Attribute> attributeClass = paramAttribute.getCategory();
		if (!isAttributeCategorySupported(attributeClass)) {
			return false;
		}
		if (paramAttribute.getCategory() == Chromaticity.class) {
			return paramAttribute == Chromaticity.COLOR;
		}
		if (paramAttribute.getCategory() == Copies.class) {
			return ((Copies) paramAttribute).getValue() == 1;
		}
		return true;
	}

	@Override
	public AttributeSet getUnsupportedAttributes(DocFlavor paramDocFlavor, AttributeSet paramAttributeSet) {
		if ((paramDocFlavor != null) && (!isDocFlavorSupported(paramDocFlavor))) {
			throw new IllegalArgumentException("flavor " + paramDocFlavor + "is not supported");
		}
		if (paramAttributeSet == null) {
			return null;
		}
		HashAttributeSet localHashAttributeSet = new HashAttributeSet();
		Attribute[] arrayOfAttribute = paramAttributeSet.toArray();
		for (int i = 0; i < arrayOfAttribute.length; i++) {
			try {
				Attribute localAttribute = arrayOfAttribute[i];
				if (!isAttributeCategorySupported(localAttribute.getCategory())) {
					localHashAttributeSet.add(localAttribute);
				} else if (!isAttributeValueSupported(localAttribute, paramDocFlavor, paramAttributeSet)) {
					localHashAttributeSet.add(localAttribute);
				}
			} catch (ClassCastException localClassCastException) {
			}
		}
		if (localHashAttributeSet.isEmpty()) {
			return null;
		}
		return localHashAttributeSet;
	}

	@Override
	public ServiceUIFactory getServiceUIFactory() {
		return new ServiceUIFactory() {

			@Override
			public String[] getUIClassNamesForRole(int role) {
				return null;
			}

			@Override
			public Object getUI(int role, String ui) {
				return null;
			}
		};
	}

}

package imeszaros.wav2hex;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class Main {

	private Shell shell;
	private Text path;
	private Text result;

	public static void main(String[] args) {
		new Main().open();
	}

	public void open() {
		final Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	protected void createContents() {
		shell = new Shell();
		shell.setImage(SWTResourceManager.getImage(Main.class, "/imeszaros/wav2hex/icon.png"));
		shell.setSize(600, 400);
		shell.setText("Wav2Hex");
		shell.setLayout(new GridLayout(2, false));

		path = new Text(shell, SWT.BORDER);
		path.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		path.setEditable(false);

		final Button browse = new Button(shell, SWT.NONE);
		browse.setText("Browse...");
		browse.addListener(SWT.Selection, e -> {
			final FileDialog fileDialog = new FileDialog(shell);

			fileDialog.setFilterNames(new String[] { "Wave files (*.wav)" });
			fileDialog.setFilterExtensions(new String[] { "*.wav" });
			fileDialog.setText("Open Wave...");

			final String result = fileDialog.open();
			if (result != null) {
				path.setText(result);
				processWave(result);
			}
		});

		result = new Text(shell, SWT.BORDER | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		result.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		result.setFont(SWTResourceManager.getFont("Consolas", 10, SWT.NORMAL));
		result.setEditable(false);

		final Button copy = new Button(shell, SWT.NONE);
		copy.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		copy.setText("Copy to Clipboard");
		copy.addListener(SWT.Selection, e -> {
			final Clipboard clipboard = new Clipboard(shell.getDisplay());
			try {
				clipboard.setContents(
						new Object[] { result.getText() },
						new Transfer[] { TextTransfer.getInstance() });

				final MessageBox info = new MessageBox(shell, SWT.OK | SWT.ICON_INFORMATION);
				info.setText("Success");
				info.setMessage("Hexa values were copied to the clipboard.");
				info.open();
			} finally {
				clipboard.dispose();
			}
		});
	}

	private void processWave(String path) {
		try {
			result.setText(new Converter(path).convert());
		} catch (Throwable t) {
			final MessageBox error = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
			error.setText("Error");
			error.setMessage("Unable to process the selected file: " + t.getMessage());
			error.open();
		}
	}
}
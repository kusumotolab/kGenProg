package jp.kusumotolab.kgenprog.output;

import java.util.List;
import jp.kusumotolab.kgenprog.Configuration;
import jp.kusumotolab.kgenprog.ga.variant.VariantStore;

public class Exporters extends Exporter {

  private List<Exporter> exporters;

  public Exporters(final Configuration config, final List<Exporter> exporters) {
    super(config);
    this.exporters = exporters;
  }

  @Override
  public void export(final VariantStore variantStore) {
    exporters.forEach(e -> e.export(variantStore));
  }
}
